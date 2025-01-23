package com.automwrite.assessment.config;

import com.automwrite.assessment.service.LlmService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public LlmService llmService() {
        return Mockito.mock(LlmService.class);
    }
}
