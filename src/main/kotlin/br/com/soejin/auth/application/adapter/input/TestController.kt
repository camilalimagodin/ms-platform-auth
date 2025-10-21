package br.com.soejin.auth.application.adapter.input

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TestController {

    @GetMapping("/secure")
    fun getSecureData(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<String> {
        val userEmail = jwt.getClaimAsString("email")
        val issuer = jwt.issuer
        val message = "Olá, $userEmail! Você foi autenticado com sucesso pelo realm: $issuer"
        return ResponseEntity.ok(message)
    }
}