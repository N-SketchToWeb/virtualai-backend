package com.nandita.virtualai.controller;

import com.nandita.virtualai.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173") // React frontend URL
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/response")
    public Mono<String> getResponse(@RequestBody PromptRequest request) {
        return aiService.getAIResponse(request.getPrompt());
    }

    // Request body class
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