package pe.breaker.dkaviplay.domain.repository

interface ImageShareRepository {
    suspend fun shareMatchQrImage(
        qrText: String,
        player1: String,
        player2: String,
        title: String = "Dkavi Play"
    )
}