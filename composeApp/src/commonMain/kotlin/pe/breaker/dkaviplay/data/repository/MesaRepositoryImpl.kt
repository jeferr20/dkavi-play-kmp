package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import pe.breaker.dkaviplay.data.remote.firebase.MesaFirebase
import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.repository.MesaRepository

class MesaRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : MesaRepository {
    override suspend fun getMesasBySede(sedeUid: String): Result<List<Mesa>> {
        return try {
            val mesasSnapshot = firestore.collection("Mesa")
                .where { "status" equalTo true }
                .where { "uuidSede" equalTo sedeUid }
                .get()

            if (mesasSnapshot.documents.isEmpty()) {
                return Result.success(emptyList())
            }
            val mesas = mesasSnapshot.documents.map { doc ->
                val data = doc.data<MesaFirebase>()
                Mesa(
                    mesaUid = doc.id,
                    nombreMesa = data.descripcion ?: "Mesa sin nombre"
                )
            }.sortedBy { it.nombreMesa }

            Result.success(mesas)
        }catch (e: Exception) {
            println("Error GET MESAS: ${e.message}")
            Result.failure(e)
        }
    }
}