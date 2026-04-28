package pe.breaker.dkaviplay.presentation.util

import android.content.Context
import android.media.MediaPlayer
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory

class AndroidAudioFactory(private val context: Context) : AudioFactory {
    private var mediaPlayer: MediaPlayer? = null

    override fun playBattleMusic() {
        // Asumiendo que tienes un archivo 'battle_music.mp3' en res/raw
        val resId = context.resources.getIdentifier("battle_music", "raw", context.packageName)

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId).apply {
                isLooping = true
                start()
            }
        }
    }

    override fun stopBattleMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}