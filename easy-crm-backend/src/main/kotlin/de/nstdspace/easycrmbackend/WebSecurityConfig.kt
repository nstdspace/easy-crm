package de.nstdspace.easycrmbackend

import logger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*


@ConfigurationProperties("cors")
data class CorsConfig(
    val allowedOrigins: List<String>
)

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    fun corsConfigurationSource(config: CorsConfig): CorsConfigurationSource {
        logger.info { "Configuring CORS for allowed origins: ${config.allowedOrigins}" }
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = config.allowedOrigins
        configuration.allowedMethods = listOf("GET", "POST")
        configuration.allowedHeaders = listOf("Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors {}
            .oauth2ResourceServer { it.jwt {} }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .build()
    }
}

val Jwt.userId: UUID
    get() = UUID.fromString(claims["sub"].toString())
