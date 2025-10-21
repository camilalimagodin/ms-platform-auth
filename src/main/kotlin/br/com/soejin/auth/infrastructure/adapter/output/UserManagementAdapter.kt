package br.com.soejin.auth.infrastructure.adapter.output

import br.com.soejin.auth.domain.command.RegisterUserCommand
import br.com.soejin.auth.domain.exception.UserRegistrationException
import br.com.soejin.auth.domain.port.output.UserManagementPort
import br.com.soejin.auth.infrastructure.adapter.output.mapper.UserRegistrationMapper
import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.springframework.stereotype.Component

@Component
class UserManagementAdapter(
    private val keycloak: Keycloak,
    private val userMapper: UserRegistrationMapper
) : UserManagementPort {

    override fun createUserIdentity(command: RegisterUserCommand) {
        val newUserRepresentation = userMapper.toUserRepresentation(command)
        val usersResource = keycloak.realm(command.tenantId).users()
        val response: Response = usersResource.create(newUserRepresentation)
        handleRegistrationResponse(response, command)
    }

    private fun handleRegistrationResponse(response: Response, command: RegisterUserCommand) {

        if (response.status == Response.Status.CREATED.statusCode) {
            println("Usuário ${command.email} criado no realm ${command.tenantId} com sucesso!")
            return
        }

        val errorDetails = response.readEntity(String::class.java)

        val userMessage = if (response.status == Response.Status.CONFLICT.statusCode) {
            "Este e-mail já está em uso. Por favor, tente outro."
        } else {
            "Não foi possível completar seu cadastro no momento. Tente novamente mais tarde."
        }

        val internalMessage = "Keycloak error (Realm: ${command.tenantId}, Status: ${response.status}, Details: $errorDetails)"

        throw UserRegistrationException(
            userMessage = userMessage,
            internalDetails = internalMessage
        )
    }
}