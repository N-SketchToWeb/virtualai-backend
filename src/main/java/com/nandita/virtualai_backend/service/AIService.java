package com.nandita.virtualai_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public AIService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public Mono<String> getAIResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            return Mono.just("Prompt is empty.");
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        return webClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/models/gemini-2.0-flash:generateContent")
                                .queryParam("key", apiKey)
                                .build()
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e ->
                        Mono.just("Gemini API error: " + e.getMessage())
                );
    }
}