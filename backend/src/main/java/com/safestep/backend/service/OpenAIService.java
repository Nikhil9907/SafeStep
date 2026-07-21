package com.safestep.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class    OpenAIService {

    private final String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public OpenAIService(
            @Value("${openai.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String explainRoute(double dangerScore, long crimeCount, long cctvCount, long lightingCount, long reportCount) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("your_key_here")) {
            return generateMockExplanation(dangerScore, crimeCount, cctvCount, lightingCount, reportCount);
        }

        String prompt = String.format(
                "A user wants to walk a route at night. The route has a Danger Score of %.1f out of 100. " +
                "Stats: Crimes nearby: %d, CCTV cameras active nearby: %d, Working streetlights: %d, Community safety reports: %d. " +
                "In 2-3 sentences, explain why this route is safe or unsafe, focusing on safety factors and giving actionable, friendly advice.",
                dangerScore, crimeCount, cctvCount, lightingCount, reportCount
        );

        try {
            MediaType jsonMediaType = MediaType.get("application/json; charset=utf-8");
            
            // Construct OpenAI Request Body
            ObjectNode requestJson = objectMapper.createObjectNode();
            requestJson.put("model", "gpt-4o-mini");
            
            var messages = requestJson.putArray("messages");
            
            var systemMsg = messages.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are the SafeStep AI assistant. You help pedestrians stay safe at night by explaining route safety details concisely.");
            
            var userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            
            requestJson.put("temperature", 0.7);
            
            String requestBodyString = objectMapper.writeValueAsString(requestJson);
            RequestBody body = RequestBody.create(requestBodyString, jsonMediaType);
            
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();
                    
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode root = objectMapper.readTree(responseBody);
                    return root.path("choices").path(0).path("message").path("content").asText().trim();
                } else {
                    String errBody = response.body() != null ? response.body().string() : "Empty body";
                    System.err.println("OpenAI API call failed with status: " + response.code() + ", error: " + errBody);
                }
            }
        } catch (IOException e) {
            System.err.println("Exception calling OpenAI API: " + e.getMessage());
        }

        // Fallback to mock explanation if API call fails
        return generateMockExplanation(dangerScore, crimeCount, cctvCount, lightingCount, reportCount);
    }

    private String generateMockExplanation(double dangerScore, long crimeCount, long cctvCount, long lightingCount, long reportCount) {
        StringBuilder explanation = new StringBuilder();
        if (dangerScore < 30) {
            explanation.append("This route is highly recommended for walking at night. ");
            if (cctvCount > 0 || lightingCount > 0) {
                explanation.append(String.format("It features solid infrastructure with %d active CCTV cameras and %d working streetlights. ", cctvCount, lightingCount));
            }
            explanation.append("There are zero reported incidents or crimes in this vicinity, ensuring a peaceful walk.");
        } else if (dangerScore < 70) {
            explanation.append("This route offers moderate safety. ");
            explanation.append(String.format("While there are %d streetlights along the way, we detected %d crime records or community safety reports nearby. ", lightingCount, crimeCount + reportCount));
            explanation.append("Stay alert and stick to the main, well-lit pavements.");
        } else {
            explanation.append("Caution: This is a high-risk route. ");
            explanation.append(String.format("It has %d crime incidents and %d safety hazard reports, with very low streetlight/CCTV coverage (%d active cameras). ", crimeCount, reportCount, cctvCount));
            explanation.append("We strongly advise choosing an alternative route or calling a ride.");
        }
        return explanation.toString();
    }
}
