package de.nstdspace.easycrmbackend

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(private val clientService: ClientService) {
    @GetMapping("clients")
    fun getClients(@AuthenticationPrincipal jwt: Jwt): List<ClientDto> =
        clientService.getClients(jwt.userId)
}

