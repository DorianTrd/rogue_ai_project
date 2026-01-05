# Rogue AI

## Context

**Rogue AI** is a cooperative multiplayer game where players must collaborate to survive 45 seconds and prevent an artificial intelligence from taking over the world. Each player controls interactive panels and commands on a cyberpunk-themed interface, executing the correct actions to keep the global threat level under control.

The game is developed in **Kotlin** with **Jetpack Compose** for the UI, and uses a backend via **WebSocket** and **REST API** for room management and game state.

---

## Project Architecture

The project follows an **MVVM** (Model-View-ViewModel) architecture with a clear separation of concerns:

- **Data Layer**
    - `GameRepository`: communicates with the backend via REST API to create and join rooms.
    - `GamePlayRepository` and `LobbyRepository`: use **RoomSocket** (WebSocket) to observe and send real-time game events.

- **Domain / Model Layer**
    - `PlayerBoard`: represents the player's board.
    - `Command` and `Instruction`: represent commands and active instructions.
    - `GameStateMessage` and `RoomInfo`: represent the overall game state and room information.

- **UI Layer**
    - **HomeScreen**: main screen for creating or joining a room.
    - **GameScreen**: main gameplay screen with commands, instructions, and gauges.
    - **ViewModels**: `HomeViewModel` and `GameViewModel` manage state and UI logic.

- **Utils**
    - `SfxManager`: manages random continuous sound effects.
    - `RoomCodeUtils`: normalizes and validates room codes.

---

## Main Dependencies

- **Kotlin / Coroutines**: asynchronous programming and flow collection.
- **Jetpack Compose**: declarative UI framework.
- **OkHttp & LoggingInterceptor**: for REST and WebSocket communication.
- **kotlinx.coroutines.flow.StateFlow**: reactive state management between ViewModel and UI.
- **Material3**: modern UI components and theming.

---

## Project Structure
```
com.example.rogue_ai_project/
│
├─ data/ # Repositories for API and WebSocket
│ ├─ GameRepository.kt
│ ├─ GamePlayRepository.kt
│ └─ LobbyRepository.kt
│
├─ model/ # Data models
│ ├─ GameModels.kt
│ ├─ LobbyModels.kt│ 
│ └─ RoomModels.kt
│ 
│
├─ network/ # Network communication
│ ├─ RoomsApi.kt
│ └─ RoomSocket.kt
│
├─ ui/
│ ├─ common/
│ │ └─ RogueAIComponents.kt
│ ├─ game/
│ │ ├─ GameOverScreen.kt
│ │ ├─ GameScreen.kt
│ │ ├─ GameViewModel.kt
│ │ └─ GameViewModelFactory.kt
│ ├─ home/
│ │ ├─ HomeScreen.kt
│ │ ├─ HomeViewModel.kt
│ │ └─ HomeViewModelFactory.kt
│ ├─ lobby/
│ │ ├─ LobbyScreen.kt
│ │ ├─ LobbyViewModel.kt
│ │ └─ LobbyViewModelFactory.kt
│ └─ theme/                 # Theme, Color, Type definitions
│
├─ util/
│ ├─ SfxManager.kt
│ └─ RoomCodeUtils.kt
│
└─ res/
├─ drawable/ # Images and assets (robot, etc.)
└─ raw/ # Sounds (among_us.mp3, brook_laughter.mp3, etc.)
```
---

## How to Play

1. Launch the application; the **HomeScreen** appears.
2. **Create a Room**: the player receives a unique room code.
3. **Join a Room**: enter a code from another player.
4. Each player sees their **command board** with interactive panels:
    - Toggle, lever, ON/OFF button, slider, custom buttons.
5. Players must execute **active instructions** in time to keep the **threat level** under control.
6. The game lasts **45 seconds**. If the threat reaches 100% or the time runs out, the game ends.
7. At the end, the game shows **victory or defeat** depending on the players' success.

---

## Lifecycle and State Management

- **ViewModel**:
    - `GameViewModel` and `HomeViewModel` manage state using `MutableStateFlow`.
    - UI Composables observe `StateFlow` via `collectAsStateWithLifecycle()` for reactive updates.

- **StateFlows used**:
    - **GameViewModel**:
        - `_commands` → list of commands on the board
        - `_instruction` → active instruction
        - `_threat` → global threat percentage
        - `_timeRemaining` → time remaining to complete instruction
        - `_gameOver` and `_victory` → game end state
    - **HomeViewModel**:
        - `isLoading` → shows loader when creating/joining a room
        - `error` → displays API error messages
        - `navigationEvent` → triggers navigation to a room

- **Coroutines & Flow Collection**:
    - `StateFlow` is collected in Composables with `collectAsStateWithLifecycle()` to ensure safe lifecycle-aware updates.
    - Player actions (`executeAction`) send events via WebSocket to update `PlayerBoard` in real-time.

---

## Visual and Audio Effects

- **Threat bars** and **timer** use Compose animations (`animateFloatAsState`, `rememberInfiniteTransition`).
- The **ROGUE AI** title has a multi-layer neon outline effect.
- `SfxManager` plays random sounds every 2–4 seconds for immersive gameplay.

---

## Technical Workflow

1. **Create / Join Room**
    - `RoomsApi` sends POST `/create-room` or GET `/room-exists/{code}`.
2. **WebSocket RoomSocket**
    - Opens a connection to receive real-time updates:
        - `PlayerBoard` → instructions and commands
        - `RoomInfo` → room data
        - `GameStateMessage` → game state (in progress, end, victory/defeat)
3. **Reactive UI**
    - `StateFlow` updates trigger automatic recomposition of Composables.
4. **Sound Effects**
    - `SfxManager` plays random sounds and stops when the game ends.

---

## Development & Build

- **Language**: Kotlin 1.9+
- **Android**: minimum SDK 21, Jetpack Compose Material3
- **IDE**: Android Studio Bumblebee or newer

**To run the project**:

```bash
git clone <repo-url>


