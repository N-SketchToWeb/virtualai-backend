package com.nandita.virtualai_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final String apiKey = System.getenv("GEMINI_API_KEY");
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

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        return webClient.post()
                .uri("/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e ->
                        Mono.just("Gemini API error: " + e.getMessage())
                );
    }
}