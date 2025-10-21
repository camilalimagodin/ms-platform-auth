package br.com.soejin.auth.domain.command

data class RegisterUserCommand(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val tenantId: String
)
