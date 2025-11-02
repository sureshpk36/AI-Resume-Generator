package com.resume.backend.AI_resume.backend.parser;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeminiResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(GeminiResponseParser.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static class ParsedResponse {
        private final JSONObject jsonObject;

        public ParsedResponse(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public JSONObject getJsonObject() {
            return jsonObject;
        }
    }

    /**
     * Extracts and parses the JSON content from Gemini API response.
     */
    public ParsedResponse parseResponse(String geminiResponse) {
        try {
            JsonNode rootNode = mapper.readTree(geminiResponse);
            // Navigate to the text node containing JSON
            JsonNode partsNode = rootNode.path("candidates").get(0)
                    .path("content").path("parts");

            String extractedJson = null;

            // Loop over parts to find JSON block
            for (JsonNode part : partsNode) {
                String text = part.path("text").asText();
                if (text.contains("{") && text.contains("}")) {
                    extractedJson = extractJsonBlock(text);
                    break;
                }
            }

            if (extractedJson == null || extractedJson.isEmpty()) {
                logger.error("No JSON block found in Gemini response");
                return new ParsedResponse(new JSONObject().put("error", "No valid JSON found"));
            }

            JSONObject jsonObject = new JSONObject(extractedJson);
            return new ParsedResponse(jsonObject);

        } catch (Exception e) {
            logger.error("Error parsing Gemini response", e);
            return new ParsedResponse(new JSONObject().put("error", e.getMessage()));
        }
    }

    /**
     * Cleans out ```json code block wrappers and extracts the actual JSON string.
     */
    private String extractJsonBlock(String text) {
        try {
            // Remove Markdown code block markers
            text = text.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            // Sometimes Gemini adds text before or after JSON → find JSON start and end
            int startIdx = text.indexOf("{");
            int endIdx = text.lastIndexOf("}");
            if (startIdx >= 0 && endIdx > startIdx) {
                return text.substring(startIdx, endIdx + 1);
            }
            return text;
        } catch (Exception e) {
            logger.error("Error extracting JSON block", e);
            return "";
        }
    }
}
