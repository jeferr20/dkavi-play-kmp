package pe.breaker.dkaviplay.presentation.animations.audio

interface AudioFactory {
    fun playBattleMusic()
    fun stopBattleMusic()
    fun pauseBattleMusic()
    fun resumeBattleMusic()
}