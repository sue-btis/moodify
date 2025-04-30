package com.haswe.moodify.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//Devolver el token del usuario usando OAuth2
// Controlador tipo REST que devuelve un JSON
@RestController
@RequiredArgsConstructor
public class OAuthController {

    //Atributo para aceder al AccessToken, PrincipalName, RefreshToken  y ClientRegistration
    @Autowired
    private final OAuth2AuthorizedClientService authorizedClientService;

    private final WebClient webclient = WebClient.create("https://api.spotify.com");

    @GetMapping("/user/profile")
    public Mono<String> getUserProfile(OAuth2AuthenticationToken authentication) {
        //obtener info del usuario autorizado
        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName()
                );

        //obtener el token
        String accessToken = client.getAccessToken().getTokenValue();

        //peticion a la api para obtener el perfil
        return webclient
                .get()
                .uri("/v1/me")
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/user/token")
    public String getToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        return "Access Token: " + client.getAccessToken().getTokenValue();
    }

}


