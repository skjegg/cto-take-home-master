package com.automwrite.assessment;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testUserRequestEndpoint() throws Exception {
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

        // Verify the recommendation file was created
        File recommendationFile = new File("recommendation.docx");
        assertTrue(recommendationFile.exists(), "Recommendation file should be created");
        assertTrue(recommendationFile.length() > 0, "Recommendation file should not be empty");

        // Clean up
        Files.deleteIfExists(recommendationFile.toPath());
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
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEndToEndWorkflow() throws Exception {
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
        File recommendationFile = new File("recommendation.docx");
        assertTrue(recommendationFile.exists(), "Recommendation file should be created");
        assertTrue(recommendationFile.length() > 0, "Recommendation file should not be empty");

        // Clean up
        Files.deleteIfExists(recommendationFile.toPath());
    }
}
