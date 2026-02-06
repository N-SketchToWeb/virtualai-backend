package com.nandita.virtualai_backend.controller;

import com.nandita.virtualai_backend.service.AIService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/response")
    public Mono<String> getResponse(@RequestBody PromptRequest request) {
        return aiService.getAIResponse(request.getPrompt());
    }

    public static class PromptRequest {
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
}