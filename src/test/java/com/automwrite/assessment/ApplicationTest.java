package com.automwrite.assessment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.automwrite.assessment.config.TestConfig;
import com.automwrite.assessment.service.LlmService;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LlmService llmService;

    private final File recommendationFile = new File("recommendation.docx");

    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(recommendationFile.toPath());
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testUserRequestEndpoint() throws Exception {
        // Mock LLM response
        String mockRecommendation = """
            Based on your request, I recommend transferring your plan from Dynamic Investment Partners to Aviva's Aggressive Growth Portfolio.
            This aligns with your investment goals and risk tolerance.
            """;
        when(llmService.generateText(anyString())).thenReturn(mockRecommendation);

        // Create a test intent file
        String userIntent = "I have a plan with Dynamic Investment Partners that I wish to transfer to Aviva into the Aggressive Growth Portfolio.";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-intent.txt",
            "text/plain",
            userIntent.getBytes()
        );

        // Send request to endpoint
        mockMvc.perform(multipart("/api/user-request")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("User request processed, recommendation created."));

        // Verify the recommendation file
        assertTrue(recommendationFile.exists(), "Recommendation file should be created");
        assertTrue(recommendationFile.length() > 0, "Recommendation file should not be empty");
    }

    @Test
    void testUserRequestWithInvalidFile() throws Exception {
        // Test with empty file
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/user-request")
                .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Empty file provided"));

        // Test with file containing only whitespace
        MockMultipartFile whitespaceFile = new MockMultipartFile(
            "file",
            "whitespace.txt",
            "text/plain",
            "   \n  \t  ".getBytes()
        );

        mockMvc.perform(multipart("/api/user-request")
                .file(whitespaceFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: No user intent found in file"));
    }

    @Test
    void testEndToEndWorkflow() throws Exception {
        // Mock LLM response with risk warnings
        String mockRecommendation = """
            While the YOLO Portfolio offers 100% crypto exposure, I must emphasize this is an ultra-high-risk strategy.
            Given your current risk profile, please consider the following warnings...
            """;
        when(llmService.generateText(anyString())).thenReturn(mockRecommendation);

        // Test with high-risk intent
        String userIntent = "I would like to roll over my stable and boring 401k with into a brokerage account and go all in on crypto";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "high-risk-intent.txt",
            "text/plain",
            userIntent.getBytes()
        );

        mockMvc.perform(multipart("/api/user-request")
                .file(file))
                .andExpect(status().isOk());

        // Verify the recommendation file
        assertTrue(recommendationFile.exists(), "Recommendation file should be created");
        assertTrue(recommendationFile.length() > 0, "Recommendation file should not be empty");
    }

    @Test
    void testErrorHandling() throws Exception {
        // Mock LLM service to throw an exception
        when(llmService.generateText(anyString())).thenThrow(new RuntimeException("LLM service error"));

        String userIntent = "Transfer my pension";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "error-test.txt",
            "text/plain",
            userIntent.getBytes()
        );

        mockMvc.perform(multipart("/api/user-request")
                .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing request: LLM service error"));
    }

    @Test
    void testFileReadError() throws Exception {
        // Test with a file that has invalid content type
        MockMultipartFile badFile = new MockMultipartFile(
            "file",
            "test.txt",
            "application/octet-stream", // Wrong content type
            new byte[] {0, 1, 2, 3} // Binary content
        );

        mockMvc.perform(multipart("/api/user-request")
                .file(badFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error reading file")));
    }
}
