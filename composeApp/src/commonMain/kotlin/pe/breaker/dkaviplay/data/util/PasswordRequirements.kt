package pe.breaker.dkaviplay.data.util

data class PasswordRequirements(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSpecialChar: Boolean = false
) {
    val isValid: Boolean
        get() = hasMinLength && hasUppercase && hasNumber && hasSpecialChar

    companion object {
        fun from(password: String): PasswordRequirements {
            return PasswordRequirements(
                hasMinLength = password.length >= 8,
                hasUppercase = password.any { it.isUpperCase() },
                hasNumber = password.any { it.isDigit() },
                hasSpecialChar = password.any { !it.isLetterOrDigit() }
            )
        }
    }
}