package br.com.soejin.auth.infrastructure.adapter.output.mapper

import br.com.soejin.auth.domain.command.RegisterUserCommand
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Component

@Component
class UserRegistrationMapper {

    fun toUserRepresentation(command: RegisterUserCommand): UserRepresentation {
        val passwordCredential = buildPasswordCredential(command.password)

        return UserRepresentation().apply {
            username = command.email
            email = command.email
            firstName = command.firstName
            lastName = command.lastName
            isEnabled = true
            credentials = listOf(passwordCredential)
        }
    }

    private fun buildPasswordCredential(password: String): CredentialRepresentation {
        return CredentialRepresentation().apply {
            type = CredentialRepresentation.PASSWORD
            value = password
            isTemporary = false
        }
    }
}