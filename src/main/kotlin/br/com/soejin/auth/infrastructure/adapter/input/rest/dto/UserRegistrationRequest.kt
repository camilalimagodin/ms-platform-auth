package br.com.soejin.auth.infrastructure.adapter.input.rest.dto

import br.com.soejin.auth.infrastructure.util.PasswordValidate

data class UserRegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
) {
    val passwordValidate: PasswordValidate = PasswordValidate()
    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        passwordValidate.validate(password)
    }
}
