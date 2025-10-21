package br.com.soejin.auth.infrastructure.config

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakAdminConfig {

    @Value("\${keycloak-admin.server-url}")
    private lateinit var serverUrl: String

    @Value("\${keycloak-admin.realm}")
    private lateinit var realm: String

    @Value("\${keycloak-admin.client-id}")
    private lateinit var clientId: String

    @Value("\${keycloak-admin.username}")
    private lateinit var username: String

    @Value("\${keycloak-admin.password}")
    private lateinit var password: String

    @Bean
    fun keycloakAdminClient(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(clientId)
            .username(username)
            .password(password)
            .build()
    }
}