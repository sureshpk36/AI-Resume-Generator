package com.resume.backend.AI_resume.backend.service;

import com.resume.backend.AI_resume.backend.parser.GeminiResponseParser;
import com.resume.backend.AI_resume.backend.repo.promptInterface;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

@Service
public class PromptService implements promptInterface {

    private static final Logger logger = LoggerFactory.getLogger(PromptService.class);

    private final GeminiResponseParser geminiResponseParser = new GeminiResponseParser();
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public PromptService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public JSONObject generateResumeResponse(String userPrompt) {
        try {
            String promptTemplate = loadFile("resume_description.txt");
            String finalPrompt = putPromptToTemplate(promptTemplate, Map.of("userDescription", userPrompt));

            String response = geminiApiCall(finalPrompt);
            logger.info("Raw Gemini Response: {}", response);

            if (response == null || response.trim().isEmpty()) {
                return createErrorResponse("Empty response from Gemini API");
            }

            GeminiResponseParser.ParsedResponse parsed = geminiResponseParser.parseResponse(response);
            JSONObject jsonObject = parsed.getJsonObject();

            if (jsonObject == null || jsonObject.isEmpty()) {
                return createErrorResponse("Failed to parse Gemini response");
            }

            return jsonObject;

        } catch (Exception e) {
            logger.error("Error generating resume response", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }

    private String geminiApiCall(String promptText) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", promptText)
                        })
                }
        );

        try {
            return webClient.post()
                    .uri(geminiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error calling Gemini API: {}", e.getMessage(), e);
            return null;
        }
    }

    private JSONObject createErrorResponse(String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("message", errorMessage);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }

    private String loadFile(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes());
        }
    }

    private String putPromptToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}
