package com.example.rogue_ai_project.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.rogue_ai_project.R
import kotlinx.coroutines.*
import kotlin.random.Random

class SfxManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<SfxType, Int>()
    private var isInitialized = false
    private var randomJob: Job? = null

    enum class SfxType {
          AMONG_US, BROOK_LAUGHTER, CLASH_ROYALE, DATTEBAYO, PIGMEN, THE_WITCHER_3
    }

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

    private fun loadSounds() {
        soundIds[SfxType.AMONG_US] = soundPool?.load(context, R.raw.among_us, 1) ?: 0
        soundIds[SfxType.BROOK_LAUGHTER] = soundPool?.load(context, R.raw.brook_laughter, 1) ?: 0
        soundIds[SfxType.CLASH_ROYALE] = soundPool?.load(context, R.raw.clash_royale, 1) ?: 0
        soundIds[SfxType.DATTEBAYO] = soundPool?.load(context, R.raw.dattebayo, 1) ?: 0
        soundIds[SfxType.PIGMEN] = soundPool?.load(context, R.raw.pigmen, 1) ?: 0
        soundIds[SfxType.THE_WITCHER_3] = soundPool?.load(context, R.raw.the_witcher_3, 1) ?: 0
    }

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

    fun stopRandomLoop() {
        randomJob?.cancel()
        randomJob = null
    }

    fun release() {
        stopRandomLoop()
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}