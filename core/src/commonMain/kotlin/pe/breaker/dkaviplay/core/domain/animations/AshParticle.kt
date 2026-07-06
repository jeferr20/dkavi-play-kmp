package pe.breaker.dkaviplay.core.domain.animations

data class AshParticle(
    val id: Int,
    val startX: Float,
    val duration: Int,
    val size: Float,
    val drift: Float,
    val rotationStart: Float
) {
    companion object {
        fun generate(id: Int, screenWidth: Float) = AshParticle(
            id = id,
            startX = (0..screenWidth.toInt()).random().toFloat(),
            duration = (3000..6000).random(),
            size = (4..10).random().toFloat(),
            drift = (-30..30).random().toFloat(),
            rotationStart = (0..360).random().toFloat()
        )
    }
}