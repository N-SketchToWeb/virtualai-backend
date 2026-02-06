package com.nandita.virtualai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AIService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public Mono<String> getAIResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            return Mono.just("Prompt is empty.");
        }

        try {
            // Gemini request body (CORRECT FORMAT)
            Map<String, Object> payload = new HashMap<>();

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart));

            payload.put("contents", List.of(content));

            return webClient.post()
                    .uri("/models/gemini-2.0-flash:generateContent")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            Map<?, ?> map = mapper.readValue(response, Map.class);
                            List<?> candidates = (List<?>) map.get("candidates");

                            if (candidates != null && !candidates.isEmpty()) {
                                Map<?, ?> first = (Map<?, ?>) candidates.get(0);
                                Map<?, ?> contentMap = (Map<?, ?>) first.get("content");
                                List<?> parts = (List<?>) contentMap.get("parts");

                                if (parts != null && !parts.isEmpty()) {
                                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                                    return part.get("text").toString();
                                }
                            }
                            return "No AI response.";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Error parsing AI response.";
                        }
                    })
                    .onErrorResume(err -> {
                        err.printStackTrace();
                        return Mono.just("AI service error: " + err.getMessage());
                    });

        } catch (Exception e) {
            return Mono.just("Failed to build AI request.");
        }
    }
}