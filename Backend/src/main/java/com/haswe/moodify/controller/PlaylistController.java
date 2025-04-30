package com.haswe.moodify.controller;


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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final WebClient webClient = WebClient.create("https://api.spotify.com");

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("spotify")
                .principal(authentication)
                .build();
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);
        if (client == null) throw new RuntimeException("Authorization failed.");
        return client.getAccessToken().getTokenValue();
    }

    @GetMapping
    public Mono<Map<String, Object>> getPlaylist(OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);

        return webClient.get()
                .uri("/v1/me/playlists")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @PostMapping("/create")
    public Mono<String> createPlaylist(
            OAuth2AuthenticationToken authentication,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean isPublic) {
        String accessToken = getAccessToken(authentication);
        return webClient.get()
                .uri("/v1/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(user -> {
                    String userId = user.get("id").toString();
                    Map<String, Object> playlistData = Map.of(
                            "name", name,
                            "public", isPublic,
                            "description", "Creada desde Moodify 🎵"
                    );

                    return webClient.post()
                            .uri("/v1/users/" + userId + "/playlists")
                            .headers(h -> h.setBearerAuth(accessToken))
                            .bodyValue(playlistData)
                            .retrieve()
                            .bodyToMono(String.class);
                });
    }

    @PostMapping("/{playlistId}/add")
    public Mono<String> addTracksToPlaylist(
            OAuth2AuthenticationToken auth,
            @PathVariable String playlistId,
            @RequestBody List<String> uris
    ) {
        String token = getAccessToken(auth);
        Map<String, Object> body = Map.of("uris", uris);

        return webClient.post()
                .uri("/v1/playlists/" + playlistId + "/tracks")
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("✅ Canciones agregadas.");
    }

    @PostMapping("/{playlistId}/play")
    public Mono<String> playPlaylist(
            OAuth2AuthenticationToken auth,
            @PathVariable String playlistId
    ){
        String token = getAccessToken(auth);

        return webClient.put()
                .uri("/v1/me/player/play")
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(Map.of("context_uri", "spotify:playlist:" + playlistId))
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("▶️ Reproduciendo playlist.");
    }

}


