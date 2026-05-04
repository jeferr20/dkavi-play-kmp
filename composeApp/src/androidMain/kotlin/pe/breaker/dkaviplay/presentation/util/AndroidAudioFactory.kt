package pe.breaker.dkaviplay.presentation.util

import android.content.Context
import android.media.MediaPlayer
import pe.breaker.dkaviplay.R
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory

class AndroidAudioFactory(
    context: Context
) : AudioFactory {

    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    override fun playBattleMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(appContext, R.raw.battle_music)?.apply {
                isLooping = true

                setOnErrorListener { mp, what, extra ->
                    mp.release()
                    mediaPlayer = null
                    true
                }

                setOnCompletionListener {
                    // Por si algún día quitas loop
                    releasePlayer()
                }
            }
        }

        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    override fun stopBattleMusic() {
        releasePlayer()
    }

    override fun pauseBattleMusic() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun resumeBattleMusic() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.run {
            try {
                if (isPlaying) stop()
            } catch (_: Exception) {
            }
            release()
        }
        mediaPlayer = null
    }
}