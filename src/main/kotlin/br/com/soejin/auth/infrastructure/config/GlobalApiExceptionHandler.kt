package br.com.soejin.auth.infrastructure.config

import br.com.soejin.auth.infrastructure.adapter.input.rest.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val dto = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message,
            details = ex.cause?.message,
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleKeycloakFailure(
        ex: IllegalStateException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val dto = ErrorResponse(
            status = HttpStatus.BAD_GATEWAY.value(),
            error = HttpStatus.BAD_GATEWAY.reasonPhrase,
            message = ex.message,
            details = ex.cause?.message,
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(dto)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()

        val dto = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Ocorreu um erro inesperado no servidor.",
            details = ex.cause?.message,
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto)
    }
}