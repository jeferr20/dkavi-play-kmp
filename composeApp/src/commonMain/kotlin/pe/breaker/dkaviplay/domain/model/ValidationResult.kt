package pe.breaker.dkaviplay.domain.model

data class ValidationResult(
    private val errorsMap: Map<String, String?> = emptyMap()
) {
    val hasErrors: Boolean = errorsMap.values.any { it != null }
    fun getError(field: String): String? = errorsMap[field]
    val errors: Map<String, String?> = errorsMap
}