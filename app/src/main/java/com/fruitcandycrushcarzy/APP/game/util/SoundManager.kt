package com.fruitcandycrushcarzy.APP.game.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.fruitcandycrushcarzy.APP.R

class SoundManager(context: Context) {
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(audioAttributes)
        .build()

    private var matchSoundId: Int = 0
    private var swapSoundId: Int = 0
    private var levelUpSoundId: Int = 0
    private var explosionSoundId: Int = 0

    init {
        // Filenames in res/raw: match.mp3, swap.mp3, levelup.mp3, explosion.mp3
        matchSoundId = soundPool.load(context, R.raw.match, 1)
        swapSoundId = soundPool.load(context, R.raw.swap, 1)
        levelUpSoundId = soundPool.load(context, R.raw.levelup, 1)
        // Fallback to match sound if explosion doesn't exist
        explosionSoundId = try {
            soundPool.load(context, R.raw.match, 1)
        } catch (e: Exception) { 0 }
    }

    fun playMatch() = playSound(matchSoundId)
    fun playSwap() = playSound(swapSoundId)
    fun playLevelUp() = playSound(levelUpSoundId)
    fun playExplosion() = playSound(explosionSoundId)

    private fun playSound(soundId: Int) {
        if (soundId != 0) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
