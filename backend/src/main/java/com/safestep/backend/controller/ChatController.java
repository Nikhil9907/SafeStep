package com.safestep.backend.controller;

import com.safestep.backend.service.OpenAIService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAIService;

    @Autowired
    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatExplainRequest {
        private double dangerScore;
        private long crimeCount;
        private long cctvCount;
        private long lightingCount;
        private long reportCount;
    }

    @PostMapping("/explain")
    public ResponseEntity<Map<String, String>> explainRoute(@RequestBody ChatExplainRequest request) {
        String explanation = openAIService.explainRoute(
                request.getDangerScore(),
                request.getCrimeCount(),
                request.getCctvCount(),
                request.getLightingCount(),
                request.getReportCount()
        );
        Map<String, String> response = new HashMap<>();
        response.put("explanation", explanation);
        return ResponseEntity.ok(response);
    }
}
