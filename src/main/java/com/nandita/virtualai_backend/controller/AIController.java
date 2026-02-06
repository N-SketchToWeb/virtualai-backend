@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // Allow Render frontend
public class AIController {

    @Autowired
    private AIService aiService;

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