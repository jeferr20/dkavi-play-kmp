package pe.breaker.dkaviplay.util

import kotlinx.cinterop.ExperimentalForeignApi
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

class IOSAudioFactory : AudioFactory {
    private var audioPlayer: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun playBattleMusic() {
        val bundle = NSBundle.mainBundle
        val path = bundle.pathForResource("battle_music", "mp3")

        if (path != null && audioPlayer == null) {
            val url = platform.Foundation.NSURL.fileURLWithPath(path)
            audioPlayer = AVAudioPlayer(url, null).apply {
                numberOfLoops = -1L // Bucle infinito en iOS
                play()
            }
        }
    }

    override fun stopBattleMusic() {
        audioPlayer?.stop()
        audioPlayer = null
    }

    override fun pauseBattleMusic() {
        TODO("Not yet implemented")
    }

    override fun resumeBattleMusic() {
        TODO("Not yet implemented")
    }
}