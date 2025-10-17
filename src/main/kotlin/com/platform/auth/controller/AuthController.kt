package com.platform.auth.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController {

    @GetMapping("/public/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "ms-platform-auth"
        )
    }

    @GetMapping("/user/info")
    fun userInfo(@AuthenticationPrincipal jwt: Jwt): Map<String, Any?> {
        return mapOf(
            "username" to jwt.claims["preferred_username"],
            "email" to jwt.claims["email"],
            "roles" to jwt.claims["realm_access"],
            "subject" to jwt.subject
        )
    }

    @GetMapping("/protected")
    fun protectedEndpoint(@AuthenticationPrincipal jwt: Jwt): Map<String, String> {
        return mapOf(
            "message" to "This is a protected endpoint",
            "user" to (jwt.claims["preferred_username"] as? String ?: "unknown")
        )
    }
}
