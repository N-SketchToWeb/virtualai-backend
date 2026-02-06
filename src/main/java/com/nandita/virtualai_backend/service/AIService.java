package com.nandita.virtualai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public Mono<String> getAIResponse(String prompt) {
        try {
            // Validate prompt
            if (prompt == null || prompt.isBlank()) {
                return Mono.just("Prompt is empty.");
            }

            // Build payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "gemini-2.0-flash");
            payload.put("temperature", 0.7);
            payload.put("maxOutputTokens", 256);

            Map<String, Object> promptItem = new HashMap<>();
            promptItem.put("type", "text");
            promptItem.put("content", prompt);

            payload.put("prompt", Collections.singletonList(promptItem));

            String jsonPayload = mapper.writeValueAsString(payload);
            System.out.println("Sending payload to Gemini: " + jsonPayload);

            // Call Gemini API
            return webClient.post()
                    .uri("/models/gemini-2.0-flash:generateText?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(jsonPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            Map<?, ?> map = mapper.readValue(response, Map.class);
                            List<?> candidates = (List<?>) map.get("candidates");
                            if (candidates != null && !candidates.isEmpty()) {
                                Map<?, ?> first = (Map<?, ?>) candidates.get(0);
                                List<?> output = (List<?>) first.get("output");
                                if (output != null && !output.isEmpty()) {
                                    Map<?, ?> out0 = (Map<?, ?>) output.get(0);
                                    return out0.get("content").toString();
                                }
                            }
                            return "No output from AI.";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Error parsing AI response.";
                        }
                    })
                    .onErrorResume(err -> {
                        err.printStackTrace();
                        return Mono.just("Failed to call AI: " + err.getMessage());
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just("Error building AI request: " + e.getMessage());
        }
    }
}