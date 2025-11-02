package com.resume.backend.AI_resume.backend.parser;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeepSeekResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekResponseParser.class);

    public static class ParsedResponse {
        private final String think;
        private final JSONObject jsonObject;

        public ParsedResponse(String think, JSONObject jsonObject) {
            this.think = think;
            this.jsonObject = jsonObject;
        }

        public String getThink() { return think; }
        public JSONObject getJsonObject() { return jsonObject; }
    }

    public ParsedResponse parseResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Response cannot be null or empty");
        }

        try {
            String thinkPart = "";
            String jsonPart = "";

            // Split the response into THINK and JSON parts
            if (response.contains("=== THINK ===") && response.contains("=== JSON ===")) {
                String[] parts = response.split("=== JSON ===");
                if (parts.length >= 2) {
                    String thinkSection = parts[0];
                    jsonPart = parts[1].trim();

                    // Extract think content
                    if (thinkSection.contains("=== THINK ===")) {
                        String[] thinkParts = thinkSection.split("=== THINK ===");
                        if (thinkParts.length >= 2) {
                            thinkPart = thinkParts[1].trim();
                        }
                    }
                }
            } else {
                // If no markers found, assume the entire response is JSON
                jsonPart = response.trim();
            }

            logger.debug("Think part: {}", thinkPart);
            logger.debug("JSON part: {}", jsonPart);

            // Parse JSON part
            JSONObject jsonObject;
            if (jsonPart.startsWith("{")) {
                jsonObject = new JSONObject(jsonPart);
            } else if (jsonPart.startsWith("[")) {
                // If it's a JSON array, wrap it in an object
                JSONArray jsonArray = new JSONArray(jsonPart);
                jsonObject = new JSONObject();
                jsonObject.put("data", jsonArray);
            } else {
                // Try to find JSON in the text
                jsonObject = extractJsonFromText(jsonPart);
            }

            return new ParsedResponse(thinkPart, jsonObject);

        } catch (JSONException e) {
            logger.error("Failed to parse JSON response: {}", e.getMessage());
            logger.error("Problematic response: {}", response);

            // Return a basic error response instead of throwing
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Failed to parse AI response");
            errorJson.put("original_response", response);
            return new ParsedResponse("", errorJson);
        }
    }

    private JSONObject extractJsonFromText(String text) {
        // Try to find JSON object in the text
        int startIdx = text.indexOf('{');
        int endIdx = text.lastIndexOf('}');

        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            String potentialJson = text.substring(startIdx, endIdx + 1);
            try {
                return new JSONObject(potentialJson);
            } catch (JSONException e) {
                logger.warn("Failed to extract JSON from text: {}", e.getMessage());
            }
        }

        // If no valid JSON found, create a basic response
        JSONObject fallback = new JSONObject();
        fallback.put("content", text);
        fallback.put("warning", "Response was not in valid JSON format");
        return fallback;
    }
}