package br.com.soejin.auth.infrastructure.util

import org.springframework.stereotype.Component

@Component
class PasswordValidate {
    companion object {
        private val PASSWORD_SPECIAL_CHARS = "!@#\$%^&*()_+-=[]{}|;:,.<>/?"
    }

    fun validate(password: String) {
        require(checkLength(password)) { "Password must have at least 8 characters" }
        require(checkUppercase(password)) { "Password must have at least 1 uppercase letter" }
        require(checkLowercase(password)) { "Password must have at least 1 lowercase letter" }
        require(checkNumber(password)) { "Password must have at least 1 number" }
        require(checkSpecialChar(password)) { "Password must have at least 1 special character: $PASSWORD_SPECIAL_CHARS" }
        require(!checkIsBlank(password)) { "Password cannot be blank" }
        require(!checkIfNull(password)) { "Password cannot be null" }
    }

    fun checkIsBlank(password: String) = password.isBlank()
    fun checkIfNull(password: String?) = password == null
    fun checkLength(password: String) = password.length >= 8
    fun checkUppercase(password: String) = password.any { it.isUpperCase() }
    fun checkLowercase(password: String) = password.any { it.isLowerCase() }
    fun checkNumber(password: String) = password.any { it.isDigit() }
    fun checkSpecialChar(password: String) = password.any { it in PASSWORD_SPECIAL_CHARS }
}