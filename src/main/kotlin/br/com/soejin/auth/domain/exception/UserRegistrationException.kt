package br.com.soejin.auth.domain.exception

class UserRegistrationException(
    val userMessage: String,
    val internalDetails: String? = null
) : RuntimeException(userMessage)