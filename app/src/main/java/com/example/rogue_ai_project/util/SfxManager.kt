package com.example.rogue_ai_project.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.rogue_ai_project.R
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * Manages short sound effects (SFX) for the game.
 *
 * This class wraps Android's SoundPool API to:
 * - load sound resources,
 * - play specific sound effects on demand,
 * - optionally play random sound effects in a loop,
 * - properly release audio resources when no longer needed.
 */
class SfxManager(private val context: Context) {
    /** SoundPool instance used for low-latency sound playback. */
    private var soundPool: SoundPool? = null

    /** Maps each SfxType to its loaded SoundPool sound ID. */
    private val soundIds = mutableMapOf<SfxType, Int>()

    /** Indicates whether the SoundPool has been successfully initialized. */
    private var isInitialized = false

    /** Coroutine job responsible for playing random sound effects. */
    private var randomJob: Job? = null

    /**
     * Enumeration of all supported sound effect types.
     *
     * Each enum value corresponds to a raw audio resource.
     */
    enum class SfxType {
          AMONG_US, BROOK_LAUGHTER, CLASH_ROYALE, DATTEBAYO, PIGMEN, THE_WITCHER_3
    }

    /**
     * List of sound effects eligible for random playback.
     */
    private val randomSfxList = listOf(
        SfxType.AMONG_US,
        SfxType.BROOK_LAUGHTER,
        SfxType.CLASH_ROYALE,
        SfxType.DATTEBAYO,
        SfxType.PIGMEN,
        SfxType.THE_WITCHER_3
    )

    init {
        initSoundPool()
        loadSounds()
    }

    /**
     * Create and configure the SoundPool instance.
     *
     * Uses GAME usage and SONIFICATION content type
     * to ensure proper audio focus and mixing behavior.
     */
    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()
        isInitialized = true
    }

    /**
     * Load all sound resources into the SoundPool.
     *
     * Each loaded sound is associated with its corresponding SfxType.
     */
    private fun loadSounds() {
        soundIds[SfxType.AMONG_US] = soundPool?.load(context, R.raw.among_us, 1) ?: 0
        soundIds[SfxType.BROOK_LAUGHTER] = soundPool?.load(context, R.raw.brook_laughter, 1) ?: 0
        soundIds[SfxType.CLASH_ROYALE] = soundPool?.load(context, R.raw.clash_royale, 1) ?: 0
        soundIds[SfxType.DATTEBAYO] = soundPool?.load(context, R.raw.dattebayo, 1) ?: 0
        soundIds[SfxType.PIGMEN] = soundPool?.load(context, R.raw.pigmen, 1) ?: 0
        soundIds[SfxType.THE_WITCHER_3] = soundPool?.load(context, R.raw.the_witcher_3, 1) ?: 0
    }


    /**
     * Start playing random sound effects at random intervals.
     *
     * Sounds are selected randomly from [randomSfxList]
     * and played every 2 to 4 seconds.
     *
     * Calling this method multiple times has no effect
     * if the loop is already running.
     */
    fun play(sfxType: SfxType, volume: Float = 1.0f) {
        if (!isInitialized) return
        soundIds[sfxType]?.let { soundId ->
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }

    fun startRandomLoop() {
        if (randomJob != null) return
        randomJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(Random.nextLong(2000, 4000))
                val randomSound = randomSfxList.random()
                play(randomSound)
            }
        }
    }

    /**
     * Stop the random sound effect loop if it is running.
     */
    fun stopRandomLoop() {
        randomJob?.cancel()
        randomJob = null
    }

    /**
     * Release all audio resources.
     *
     * This should be called when leaving the app or
     * when sound effects are no longer needed.
     */
    fun release() {
        stopRandomLoop()
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}