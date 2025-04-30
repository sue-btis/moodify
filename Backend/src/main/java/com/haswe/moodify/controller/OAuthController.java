package com.haswe.moodify.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//Devolver el token del usuario usando OAuth2
// Controlador tipo REST que devuelve un JSON
@RequiredArgsConstructor
@RestController
public class OAuthController {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final WebClient webClient = WebClient.create("https://api.spotify.com");

    @GetMapping("/user/profile")
    public Mono<String> getUserProfile(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
        if (client == null) throw new RuntimeException("Token no válido o sesión expirada");

        String token = client.getAccessToken().getTokenValue();

        return webClient
                .get()
                .uri("/v1/me")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/user/token")
    public String getToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
        if (client == null) return "No autorizado";

        return "Access Token: " + client.getAccessToken().getTokenValue();
    }
}


