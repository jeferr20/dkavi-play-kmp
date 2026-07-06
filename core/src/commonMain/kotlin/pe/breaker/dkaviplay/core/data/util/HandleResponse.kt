package pe.breaker.dkaviplay.core.data.util

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T, R> handleResponse(
    response: HttpResponse,
    onSuccess: (T) -> Result<R>
): Result<R> {
    return try {
        val apiResponse = response.body<pe.breaker.dkaviplay.core.data.remote.ApiResponseDto<T>>()

        if (response.status.isSuccess() && apiResponse.ok) {
            val resultData = if (T::class == String::class && apiResponse.data == null)
                apiResponse.message as? T
            else
                apiResponse.data

            if (resultData != null)
                onSuccess(resultData)
            else
                Result.failure(Exception("Respuesta vacía del servidor"))
        } else {
            val errorMsg = apiResponse.message ?: "Error desconocido (${response.status.value})"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: NoTransformationFoundException) {
        Result.failure(Exception("Error de formato: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(Exception("Error de red: ${e.message}"))
    }
}