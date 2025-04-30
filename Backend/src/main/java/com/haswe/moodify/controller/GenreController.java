/*package com.haswe.moodify.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GenreController {

    private final WebClient webClient = WebClient.create("https://api.spotify.com");

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @GetMapping("/genres")
    public Map<String, List<String>> getGenres(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            throw new RuntimeException("Unauthorized: Could not authorize client.");
        }

        String token = client.getAccessToken().getTokenValue();

        // Consulta la API de recomendaciones para obtener géneros disponibles
        Map<String, Object> response = webClient.get()
                .uri("https://api.spotify.com/v1/recommendations/available-genre-seeds")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<String> genres = (List<String>) response.getOrDefault("genres", new ArrayList<>());

        return Map.of("genres", genres.stream().sorted().collect(Collectors.toList()));
    }
}
*/