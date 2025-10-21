package br.com.soejin.auth.domain.port.output

import br.com.soejin.auth.domain.command.RegisterUserCommand

interface UserManagementPort {
    fun createUserIdentity(command: RegisterUserCommand) // <-- Verbo mais específico!
}