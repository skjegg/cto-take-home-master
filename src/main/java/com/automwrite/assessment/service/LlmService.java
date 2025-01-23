package com.automwrite.assessment.service;

import java.util.concurrent.CompletableFuture;

public interface LlmService {

    String generateText(String prompt);

    CompletableFuture<String> generateTextAsync(String prompt);
}
