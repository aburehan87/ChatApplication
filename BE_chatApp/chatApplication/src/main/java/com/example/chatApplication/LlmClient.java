package com.example.chatApplication;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "llmClient", url = "${llm.api.url}", configuration = com.example.chatApplication.FeignConfig.class)
public interface LlmClient {

    @PostMapping("/generate")  // Adjust to the actual API path
    String generateResponse(@RequestBody Map<String, String> request);
}

