package com.example.rogue_ai_project.network

import com.example.rogue_ai_project.model.Board
import com.example.rogue_ai_project.model.Command
import com.example.rogue_ai_project.model.GameStateMessage
import com.example.rogue_ai_project.model.Instruction
import com.example.rogue_ai_project.model.PlayerBoard
import com.example.rogue_ai_project.model.PlayerInfo
import com.example.rogue_ai_project.model.RoomInfo
import com.example.rogue_ai_project.model.TryHistoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Manages a WebSocket connection to a game room backend.
 * Provides state flows for connection status, room info, game state, player board and errors.
 */
class RoomSocket(
    private val client: OkHttpClient = defaultClient(),
    private val baseUrl: String = "wss://backend.rogueai.surpuissant.io"
) {
    private var webSocket: WebSocket? = null
    private var scope: CoroutineScope? = null

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo: StateFlow<RoomInfo?> = _roomInfo.asStateFlow()

    private val _gameState = MutableStateFlow<GameStateMessage?>(null)
    val gameState: StateFlow<GameStateMessage?> = _gameState.asStateFlow()

    private val _playerBoard = MutableStateFlow<PlayerBoard?>(null)
    val playerBoard: StateFlow<PlayerBoard?> = _playerBoard.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Open a WebSocket connection for the given room code and attach the provided coroutine scope.
     * Creates a listener that updates the internal state flows when messages arrive.
     *
     * @param roomCode room identifier used as query parameter on the WebSocket URL.
     * @param coroutineScope scope used to launch coroutine work for incoming messages and callbacks.
     */
    fun openRoomConnection(roomCode: String, coroutineScope: CoroutineScope) {
        scope = coroutineScope
        closeRoomConnection()

        val request = Request.Builder()
            .url("$baseUrl/?room=$roomCode")
            .build()

        val listener = RoomWebSocketListener(
            scope = coroutineScope,
            onRoomInfo = { info -> _roomInfo.value = info },
            onGameState = { state -> _gameState.value = state },
            onPlayerBoard = { board -> _playerBoard.value = board },
            onError = { err -> _error.value = err },
            onOpenChanged = { isOpen -> _connected.value = isOpen }
        )

        webSocket = client.newWebSocket(request, listener)
    }
    /**
     * Close the current WebSocket connection if any.
     *
     * @param code WebSocket close code (default 1000 normal closure).
     * @param reason textual reason for closing.
     */
    fun closeRoomConnection(code: Int = 1000, reason: String = "client closing") {
        webSocket?.close(code, reason)
        webSocket = null
        _connected.value = false
    }

    /**
     * Send a ready/unready message to the room via WebSocket.
     *
     * @param ready true if player is ready, false otherwise.
     * @return true if the message was queued/sent, false if no websocket is available.
     */
    fun sendReady(ready: Boolean): Boolean {
        val msg = JSONObject()
            .put("type", "room")
            .put("payload", JSONObject().put("ready", ready))
            .toString()
        return webSocket?.send(msg) ?: false
    }

    /**
     * Send a refresh_name request to the server to update the player's display name.
     *
     * @return true if the message was queued/sent, false if no websocket is available.
     */
    fun refreshName(): Boolean {
        val msg = JSONObject()
            .put("type", "refresh_name")
            .toString()
        return webSocket?.send(msg) ?: false
    }

    /**
     * Send an execute_action message for a specific command.
     *
     * @param commandId id of the command to execute.
     * @param action the action name to perform.
     * @return true if the message was queued/sent, false if no websocket is available.
     */
    fun sendExecuteAction(commandId: String, action: String): Boolean {
        val payload = JSONObject()
            .put("command_id", commandId)
            .put("action", action)
        val msg = JSONObject()
            .put("type", "execute_action")
            .put("payload", payload)
            .toString()
        return webSocket?.send(msg) ?: false
    }

    /**
     * Internal WebSocket listener mapping raw JSON messages to typed model updates.
     * All callbacks use the provided CoroutineScope to dispatch updates.
     */
    private inner class RoomWebSocketListener(
        private val scope: CoroutineScope,
        private val onRoomInfo: (RoomInfo) -> Unit,
        private val onGameState: (GameStateMessage) -> Unit,
        private val onPlayerBoard: (PlayerBoard) -> Unit,
        private val onError: (String) -> Unit,
        private val onOpenChanged: (Boolean) -> Unit
    ) : WebSocketListener() {

        /**
         * Called when the WebSocket is successfully opened.
         * Notifies observers that the connection is open.
         */
        override fun onOpen(webSocket: WebSocket, response: Response) {
            scope.launch { onOpenChanged(true) }
        }

        /**
         * Called when a text message is received from the server.
         * Parses the JSON and delegates to specific parsers based on the "type" field.
         *
         * Exceptions during parsing are forwarded to the onError callback.
         */
        override fun onMessage(webSocket: WebSocket, text: String) {
            scope.launch {
                try {
                    val root = JSONObject(text)
                    when (root.optString("type")) {
                        "room_info" -> parseRoomInfo(root)
                        "game_state" -> parseGameState(root)
                        "player_board" -> parsePlayerBoard(root)
                        else -> Unit
                    }
                } catch (t: Throwable) {
                    onError(t.message ?: "Failed to parse message")
                }
            }
        }

        /**
         * Called when the WebSocket is closed from the remote side.
         * Updates the open state to false.
         */
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            scope.launch { onOpenChanged(false) }
        }

        /**
         * Called when the WebSocket encounters a failure.
         * Reports the failure message and sets the connection state to closed.
         */
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            scope.launch {
                onOpenChanged(false)
                onError(t.message ?: "WebSocket failure")
            }
        }

        /**
         * Parse a `room_info` message payload into a RoomInfo model and emit it.
         *
         * The method safely accesses fields using opt\* methods and returns early on missing required nodes.
         */
        private fun parseRoomInfo(root: JSONObject) {
            val payload = root.optJSONObject("payload") ?: return
            val youObj = payload.optJSONObject("you") ?: return
            val playersArray = payload.optJSONArray("players") ?: return

            val you = PlayerInfo(
                id = youObj.optString("id"),
                name = youObj.optString("name"),
                ready = youObj.optBoolean("ready")
            )

            val players = mutableListOf<PlayerInfo>()
            for (i in 0 until playersArray.length()) {
                val p = playersArray.getJSONObject(i)
                players.add(
                    PlayerInfo(
                        id = p.optString("id"),
                        name = p.optString("name"),
                        ready = p.optBoolean("ready")
                    )
                )
            }

            onRoomInfo(
                RoomInfo(
                    you = you,
                    players = players,
                    roomState = payload.optString("room_state"),
                    level = payload.optInt("level", 1)
                )
            )
        }

        /**
         * Parse a `game_state` message payload into a GameStateMessage model and emit it.
         *
         * Handles optional fields like duration, start_threat, game_duration and win.
         * Also maps tryHistory array into TryHistoryItem list if present.
         */
        private fun parseGameState(root: JSONObject) {
            val payload = root.optJSONObject("payload") ?: return
            val state = payload.optString("state")

            val tryHistory = payload.optJSONArray("tryHistory")?.let { arr ->
                (0 until arr.length()).map { i ->
                    val item = arr.getJSONObject(i)
                    TryHistoryItem(
                        time = item.optLong("time"),
                        playerId = item.optString("player_id"),
                        success = item.optBoolean("success")
                    )
                }
            }


            val winValue = if (payload.has("win")) {
                payload.getBoolean("win")
            } else {
                null
            }

            val gameStateMsg = GameStateMessage(
                state = state,
                duration = if (payload.has("duration")) payload.getInt("duration") else null,
                startThreat = if (payload.has("start_threat")) payload.getInt("start_threat") else null,
                gameDuration = if (payload.has("game_duration")) payload.getInt("game_duration") else null,
                win = winValue,
                tryHistory = tryHistory
            )

            onGameState(gameStateMsg)
        }

        /**
         * Parse a `player_board` message payload into a PlayerBoard model and emit it.
         *
         * Converts command list and instruction object into typed models. Returns early if required nodes are missing.
         */
        private fun parsePlayerBoard(root: JSONObject) {
            val payload = root.optJSONObject("payload") ?: return
            val boardObj = payload.optJSONObject("board") ?: return
            val commandsArray = boardObj.optJSONArray("commands") ?: return
            val instructionObj = payload.optJSONObject("instruction") ?: return

            val commands = mutableListOf<Command>()
            for (i in 0 until commandsArray.length()) {
                val c = commandsArray.getJSONObject(i)
                val actionsArray = c.optJSONArray("action_possible") ?: continue
                val actions = (0 until actionsArray.length()).map { actionsArray.getString(it) }

                commands.add(
                    Command(
                        id = c.optString("id"),
                        name = c.optString("name"),
                        type = c.optString("type"),
                        styleType = c.optString("styleType"),
                        actualStatus = c.optString("actual_status"),
                        actionPossible = actions
                    )
                )
            }

            onPlayerBoard(
                PlayerBoard(
                    board = Board(commands),
                    instruction = Instruction(
                        commandId = instructionObj.optString("command_id"),
                        timeout = instructionObj.optLong("timeout"),
                        timestampCreation = instructionObj.optLong("timestampCreation"),
                        commandType = instructionObj.optString("command_type"),
                        instructionText = instructionObj.optString("instruction_text"),
                        expectedStatus = instructionObj.optString("expected_status")
                    ),
                    threat = payload.optInt("threat")
                )
            )
        }
    }

    companion object {

        /**
         * Create a default OkHttpClient for WebSocket usage.
         *
         * - sets a basic logging interceptor,
         * - uses a short connect timeout,
         * - disables read timeout (websocket keeps connection open),
         * - sets a ping interval to keep the socket alive.
         */
        private fun defaultClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .pingInterval(20, TimeUnit.SECONDS)
                .build()
        }
    }

    /**
     * Reset all internal state and close any open connection.
     * Useful when leaving a room or logging out.
     */
    fun resetAll() {
        closeRoomConnection()
        _roomInfo.value = null
        _gameState.value = null
        _playerBoard.value = null
        _error.value = null
        _connected.value = false
    }
}
