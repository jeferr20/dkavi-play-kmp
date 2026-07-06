package pe.breaker.dkaviplay.core.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.core.data.mapper.toDomain
import pe.breaker.dkaviplay.core.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.core.data.util.UserSessionManager
import pe.breaker.dkaviplay.core.domain.model.UserQuick
import pe.breaker.dkaviplay.core.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.core.util.obtenerNombreDiaActual
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class QuickPlayRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val sessionManager: UserSessionManager,
) : QuickPlayRepository {

    override suspend fun searchUsers(userToSearch: String): Result<List<UserQuick>> {
        return try {
            val currentUserUid = sessionManager.getUserUid()
            val currentDepartamento = sessionManager.getCurrentUsuario()?.departamento
            val currentProvincia = sessionManager.getCurrentUsuario()?.provincia
            val today = obtenerNombreDiaActual()

            val query = firestore.collection("UserMovil")
                .where { "status" equalTo true }
                .where {"departamento" equalTo currentDepartamento }
                .where {"provincia" equalTo currentProvincia }
                .run {
                    if (userToSearch.isNotBlank()) {
                        where { "user" greaterThanOrEqualTo userToSearch }
                            .where { "user" lessThanOrEqualTo userToSearch + "\uf8ff" }
                    } else this
                }

            val snapshot = query.get()

            val users = snapshot.documents.mapNotNull { document ->
                if (document.id == currentUserUid) return@mapNotNull null
                val userFirebase = document.data<UserMovilFirebase>() ?: return@mapNotNull null

                val horarioValido = userFirebase.horarios?.any { horario ->
                    horario.nombre.equals(today, ignoreCase = true) &&
                            horario.habilitado == true &&
                            estaDentroDelHorario(horario.horaInicio, horario.horaFin)
                } ?: false

                if (!horarioValido) return@mapNotNull null

                userFirebase.copy(
                    userUid = document.id
                ).toDomain()
            }
            Result.success(users)
        } catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUser(userUid: String): Result<UserQuick> {
        return try{
            val snapshot = firestore.collection("UserMovil").document(userUid).get()
            if (!snapshot.exists) {
                return Result.failure(Exception("El usuario no existe en la base de datos."))
            }
            val responseDto = snapshot.data<UserMovilFirebase>()
            val user = responseDto.toDomain().copy(userUid = userUid)
            Result.success(user)
        }catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun estaDentroDelHorario(inicio: String?, fin: String?): Boolean {
        return try {
            if(inicio.isNullOrEmpty() || fin.isNullOrEmpty()) return false
            val ahora = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .time

            val horaInicio = LocalTime.parse(inicio)
            val horaFin = LocalTime.parse(fin)

            ahora in horaInicio..horaFin

        } catch (e: Exception) {
            println("Error parseando horas: $inicio - $fin. Detalle: ${e.message}")
            false
        }
    }
}