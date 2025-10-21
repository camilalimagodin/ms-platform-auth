package br.com.soejin.auth.domain.port.input

import br.com.soejin.auth.domain.command.RegisterUserCommand

interface RegisterUserUseCase {
    fun execute(command: RegisterUserCommand)
}