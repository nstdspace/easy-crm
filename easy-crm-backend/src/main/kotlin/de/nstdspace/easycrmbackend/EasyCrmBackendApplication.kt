package de.nstdspace.easycrmbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    CorsConfig::class
)
class EasyCrmBackendApplication

fun main(args: Array<String>) {
    runApplication<EasyCrmBackendApplication>(*args)
}
