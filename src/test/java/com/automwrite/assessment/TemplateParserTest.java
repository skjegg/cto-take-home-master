package com.automwrite.assessment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.automwrite.assessment.service.IntentProcessingService;
import com.automwrite.assessment.service.util.DocumentService;

@SpringBootTest
public class TemplateParserTest {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private IntentProcessingService intentProcessingService;
    
    private XWPFDocument template;

    @BeforeEach
    void setUp() throws IOException {
        template = documentService.loadTemplate();
    }

    @Test
    void testTemplateLoading() {
        assertNotNull(template, "Template should be loaded successfully");
    }

    @Test
    void testRecommendationInsertion() throws IOException {
        String testRecommendation = "Test recommendation text";
        documentService.insertRecommendation(template, testRecommendation);
        
        boolean found = false;
        for (var paragraph : template.getParagraphs()) {
            if (paragraph.getText().contains(testRecommendation)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Recommendation should be inserted into the template");
    }

    @Test
    void testIntentProcessing() throws IOException {
        String userIntent = "I have a plan with Dynamic Investment Partners that I wish to transfer to Aviva into the Aggressive Growth Portfolio.";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        assertNotNull(recommendation, "Recommendation should not be null");
        assertTrue(recommendation.length() > 0, "Recommendation should not be empty");
        
        // The recommendation should mention key elements from the intent
        assertTrue(recommendation.contains("Dynamic Investment Partners"), "Should mention source provider");
        assertTrue(recommendation.contains("Aviva"), "Should mention target provider");
        assertTrue(recommendation.contains("Aggressive Growth Portfolio"), "Should mention target portfolio");
        
        // Should not contain any letter formatting elements
        assertFalse(recommendation.contains("Dear"), "Should not contain greeting");
        assertFalse(recommendation.contains("Sincerely"), "Should not contain closing");
        assertFalse(recommendation.matches(".*\\d{1,2}/\\d{1,2}/\\d{2,4}.*"), "Should not contain date");
    }

    @Test
    void testRiskBasedIntent() throws IOException {
        String userIntent = "I want to transfer my plan with Secure Retirement Solutions to a Transact platform in a suitable risk profile investment.";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        assertNotNull(recommendation, "Recommendation should not be null");
        assertTrue(recommendation.length() > 0, "Recommendation should not be empty");
        
        // Should include risk assessment and suitable portfolio recommendation
        assertTrue(recommendation.contains("risk"), "Should discuss risk profile");
        assertTrue(recommendation.contains("Transact"), "Should mention target platform");
        
        // Should not contain any letter formatting elements
        assertFalse(recommendation.contains("Best regards"), "Should not contain closing");
        assertFalse(recommendation.contains("From:"), "Should not contain letter header");
    }

    @Test
    void testHighRiskIntent() throws IOException {
        String userIntent = "I would like to roll over my stable and boring 401k with into a brokerage account and go all in on crypto";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        assertNotNull(recommendation, "Recommendation should not be null");
        assertTrue(recommendation.length() > 0, "Recommendation should not be empty");
        
        // Should include risk warnings and YOLO portfolio details
        assertTrue(recommendation.contains("risk"), "Should discuss risk");
        assertTrue(recommendation.contains("warning"), "Should include risk warnings");
        assertTrue(recommendation.contains("YOLO Portfolio"), "Should mention YOLO portfolio");
        
        // Should not contain any letter formatting elements
        assertFalse(recommendation.contains("Yours"), "Should not contain closing");
        assertFalse(recommendation.contains("Subject:"), "Should not contain letter header");
    }

    @Test
    void testRecommendationCleaning() throws IOException {
        // Test with a recommendation that includes unwanted elements
        String userIntent = "I want to transfer my pension to Aviva";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        // Verify cleaning of various unwanted elements
        assertFalse(recommendation.contains("Dear"), "Should not contain greeting");
        assertFalse(recommendation.contains("Sincerely"), "Should not contain closing");
        assertFalse(recommendation.contains("Date:"), "Should not contain date header");
        assertFalse(recommendation.contains("From:"), "Should not contain from header");
        assertFalse(recommendation.contains("To:"), "Should not contain to header");
        assertFalse(recommendation.matches(".*\\d{1,2}/\\d{1,2}/\\d{2,4}.*"), "Should not contain date pattern");
        assertFalse(recommendation.matches("(?i).*regards.*"), "Should not contain regards");
        
        // Verify that important content is preserved
        assertTrue(recommendation.contains("Aviva"), "Should preserve platform name");
        assertTrue(recommendation.contains("transfer"), "Should preserve intent");
    }
}
