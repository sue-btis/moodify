package com.haswe.moodify.controller;

import com.haswe.moodify.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final WebClient webClient = WebClient.create("https://api.spotify.com");

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @GetMapping("/search")
    public Mono<List<Song>> searchSongs(
            OAuth2AuthenticationToken authentication,
            @RequestParam("q") String searchQuery,
            @RequestParam("type") String searchType
    ) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            return Mono.error(new RuntimeException("Client authorization failed. Please log in again."));
        }

        String token = client.getAccessToken().getTokenValue();

        String apiType;
        if ("song".equalsIgnoreCase(searchType)) {
            apiType = "track";
        } else if ("playlist".equalsIgnoreCase(searchType)) {
            apiType = "playlist";
        } else {
            return Mono.error(new IllegalArgumentException("Tipo de búsqueda inválido: " + searchType));
        }

        System.out.println("🔍 Buscando en Spotify: " + searchQuery + " | Tipo: " + apiType);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search")
                        .queryParam("q", searchQuery)
                        .queryParam("type", apiType)
                        .queryParam("market", "US")
                        .queryParam("limit", 50)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            System.err.println("❌ Spotify API error: " + body);
                            return Mono.error(new RuntimeException("Spotify API error"));
                        }))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> extractItemsFromResponse(response, apiType));
    }


    private List<Song> extractItemsFromResponse(Map<String, Object> response, String apiType) {
        String responseKey = apiType + "s"; // "tracks" o "playlists"
        Map<String, Object> container = (Map<String, Object>) response.get(responseKey);

        if (container == null || !container.containsKey("items")) {
            System.err.println("❌ No '" + responseKey + "' found in response.");
            return List.of();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) container.get("items");

        if (items == null || items.isEmpty()) {
            System.out.println("ℹ️ Spotify respondió sin resultados.");
            return List.of();
        }

        return items.stream()
                .map(item -> tryParseSong(item, apiType))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private Song tryParseSong(Map<String, Object> item, String apiType) {
        try {
            String id = (String) item.get("id");
            String uri = (String) item.get("uri");
            String title = (String) item.get("name");

            if (id == null || uri == null || title == null) {
                return null;
            }

            String artist = "Playlist";
            String imageUrl = "https://via.placeholder.com/300x300?text=No+Image";

            if ("track".equals(apiType)) {
                List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
                artist = artists.isEmpty() ? "Unknown Artist" : (String) artists.get(0).get("name");

                Map<String, Object> album = (Map<String, Object>) item.get("album");
                if (album != null) {
                    List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
                    if (images != null && !images.isEmpty()) {
                        imageUrl = (String) images.get(0).get("url");
                    }
                }
            } else if ("playlist".equals(apiType)) {
                List<Map<String, Object>> images = (List<Map<String, Object>>) item.get("images");
                if (images != null && !images.isEmpty()) {
                    imageUrl = (String) images.get(0).get("url");
                }
            }

            return new Song(id, title, artist, imageUrl, uri);
        } catch (Exception e) {
            System.err.println("❌ Error al procesar item Spotify: " + e.getMessage());
            return null;
        }
    }



}
