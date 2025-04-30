/*package com.haswe.moodify.controller;

import com.haswe.moodify.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final WebClient webClient = WebClient.create("https://api.spotify.com");

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @GetMapping
    public Mono<Map<String, List<Song>>> search(
            OAuth2AuthenticationToken authentication,
            @RequestParam String prompt,
            @RequestParam(defaultValue = "track") String type
    ) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

        if (client == null) {
            return Mono.error(new RuntimeException("Client authorization failed."));
        }

        String token = client.getAccessToken().getTokenValue();

        String encodedPrompt = prompt;

        String searchType = switch (type) {
            case "playlist" -> "playlist";
            case "all" -> "track,playlist";
            default -> "track";
        };

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search")
                        .queryParam("q", prompt) // NO ENCODE MANUAL!
                        .queryParam("type", searchType)
                        .queryParam("limit", 10)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, List<Song>> result = new HashMap<>();
                    if (response.get("tracks") instanceof Map trackMap)
                        result.put("tracks", extractSongs(trackMap));
                    if (response.get("playlists") instanceof Map playlistMap)
                        result.put("playlists", extractPlaylists(playlistMap));
                    return result;
                });

    }

    private List<Song> extractSongs(Map<String, Object> tracks) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) tracks.getOrDefault("items", Collections.emptyList());
        return items.stream().map(item -> {
            String title = (String) item.get("name");
            String artist = ((List<Map<String, Object>>) item.get("artists")).get(0).get("name").toString();
            String imageUrl = extractImageUrl(item.get("album"));
            String uri = (String) item.get("uri");
            return new Song(title, artist, imageUrl, uri);
        }).collect(Collectors.toList());
    }

    private List<Song> extractPlaylists(Map<String, Object> playlists) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) playlists.getOrDefault("items", Collections.emptyList());
        return items.stream().map(item -> {
            String title = (String) item.get("name");
            String creator = ((Map<String, Object>) item.get("owner")).get("display_name").toString();
            String imageUrl = extractImageUrl(item);
            String uri = (String) item.get("uri");
            return new Song(title, creator, imageUrl, uri);
        }).collect(Collectors.toList());
    }

    private String extractImageUrl(Object objWithImages) {
        if (objWithImages instanceof Map<?, ?> map) {
            Object imagesObj = map.get("images");
            if (imagesObj instanceof List<?> imagesList && !imagesList.isEmpty()) {
                Object firstImage = imagesList.get(0);
                if (firstImage instanceof Map<?, ?> imageMap) {
                    Object url = imageMap.get("url");
                    if (url != null) {
                        return url.toString();
                    }
                }
            }
        }
        return "https://via.placeholder.com/300x300?text=No+Image";
    }
}
*/