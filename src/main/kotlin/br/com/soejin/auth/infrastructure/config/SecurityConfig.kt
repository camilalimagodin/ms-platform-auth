package br.com.soejin.auth.infrastructure.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.web.SecurityFilterChain
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${auth-server-url}")
    private lateinit var keycloakBaseUrl: String
    private val authenticationManagers = ConcurrentHashMap<String, AuthenticationManager>()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.authenticationManagerResolver(authenticationManagerResolver())
            }

        return http.build()
    }

    private fun authenticationManagerResolver(): AuthenticationManagerResolver<HttpServletRequest> {
        return AuthenticationManagerResolver { request ->
            val tenantId = request.getHeader("X-Tenant-ID")
                ?: throw IllegalArgumentException("Header X-Tenant-ID não encontrado!")

            authenticationManagers.computeIfAbsent(tenantId) { tenant ->
                val issuerUri = "$keycloakBaseUrl/realms/$tenant"

                val jwtDecoder: JwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri)
                val authenticationProvider = JwtAuthenticationProvider(jwtDecoder)

                AuthenticationManager { authentication ->
                    authenticationProvider.authenticate(authentication)
                }
            }
        }
    }
}