package br.com.soejin.auth.domain.usecase

import br.com.soejin.auth.domain.command.RegisterUserCommand
import br.com.soejin.auth.domain.port.input.RegisterUserUseCase
import br.com.soejin.auth.domain.port.output.UserManagementPort

import org.springframework.stereotype.Service

@Service
class RegisterUserUseCaseImpl(
    private val userManagementPort: UserManagementPort
) : RegisterUserUseCase {

    override fun execute(command: RegisterUserCommand) = userManagementPort.createUserIdentity(command)
}