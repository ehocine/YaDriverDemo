package com.hocel.demodriver.common

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.hocel.demodriver.R
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RingtoneManager @Inject constructor(
    private val application: Application,
    private val scope: CoroutineScope,
) {
    private val context: Context get() = application.applicationContext
    private val ringingDuration: Long = 30 * 1000

    private var _player: MediaPlayer? = null
    private val player: MediaPlayer?
        get() {
            if (_player == null) {
                _player = MediaPlayer.create(context, R.raw.ringtone_new_trip)
                val audioManager: AudioManager? =
                    context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                val originalVolume: Int =
                    audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
                val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 5
                audioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    if (originalVolume < maxVolume / 2) maxVolume / 2 else originalVolume, 0
                )
                _player?.isLooping = true
            }
            return _player
        }
    private var ringtoneKillerJob: Job? = null

    fun startRinging() {
        if (player?.isPlaying == false) {
            player?.start()
            autoStopRinging()
        }
    }

    private fun autoStopRinging() {
        ringtoneKillerJob = scope.launch(context = Dispatchers.IO) {
            delay(ringingDuration)
            stopRinging()
        }
    }
    fun stopRinging() {
        player?.release()
        ringtoneKillerJob?.cancel()
        _player = null
    }
}