package br.com.soejin.auth.infrastructure.adapter.input.rest

import br.com.soejin.auth.domain.command.RegisterUserCommand
import br.com.soejin.auth.domain.port.input.RegisterUserUseCase
import br.com.soejin.auth.infrastructure.adapter.input.rest.dto.MessegeResponse
import br.com.soejin.auth.infrastructure.adapter.input.rest.dto.UserRegistrationRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/register")
class UserRegistrationController(
    private val registerUserUseCase: RegisterUserUseCase
) {
    @PostMapping
    fun registerUser(
        @RequestHeader("X-Tenant-ID") tenantId: String,
        @RequestBody request: UserRegistrationRequest
    ): ResponseEntity<MessegeResponse> {
        val command = RegisterUserCommand(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = request.password,
            tenantId = tenantId
        )

        registerUserUseCase.execute(command)

        return ResponseEntity
            .created(URI("/api/v1/users?tenantId=$tenantId"))
            .body(
                MessegeResponse(
                    201,
                    "Usuário criado com sucesso no realm $tenantId",
                    LocalDateTime.now()
                )
            )
    }
}