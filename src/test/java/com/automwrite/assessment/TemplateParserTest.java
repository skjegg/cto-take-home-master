package com.automwrite.assessment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.automwrite.assessment.config.TestConfig;
import com.automwrite.assessment.service.IntentProcessingService;
import com.automwrite.assessment.service.LlmService;
import com.automwrite.assessment.service.util.DocumentService;

@SpringBootTest
@Import(TestConfig.class)
public class TemplateParserTest {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private IntentProcessingService intentProcessingService;
    
    @Autowired
    private LlmService llmService;
    
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
        // Mock LLM response
        String mockRecommendation = """
            Based on your request, I recommend transferring your plan from Dynamic Investment Partners to Aviva's Aggressive Growth Portfolio.
            This aligns with your investment goals and risk tolerance.
            """;
        when(llmService.generateText(anyString())).thenReturn(mockRecommendation);

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
        // Mock LLM response
        String mockRecommendation = """
            Given your moderate risk tolerance, I recommend transferring your plan from Secure Retirement Solutions to the Transact platform.
            The Balanced Growth Portfolio would be suitable for your risk profile.
            """;
        when(llmService.generateText(anyString())).thenReturn(mockRecommendation);

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
        // Mock LLM response with risk warnings and YOLO portfolio details
        String mockRecommendation = """
            WARNING: This recommendation concerns an ultra-high-risk investment strategy.
            
            Based on your request to invest in cryptocurrency, I must discuss the YOLO Portfolio option. This portfolio consists of 100% alternative investments focused on cryptocurrency, making it our highest-risk investment option.
            
            Given your current risk profile of Moderate and investment horizon of 10-15 years, this represents a significant deviation from your usual investment approach. The YOLO Portfolio requires a minimum investment of £75,000 and carries an annual management charge of 5.95%.
            
            Key risk warnings:
            1. This strategy carries an extremely high risk of capital loss
            2. Cryptocurrency investments are highly volatile
            3. This represents a significant deviation from your stated risk tolerance
            
            Alternative recommendation: Consider the Aggressive Growth Portfolio which offers exposure to alternative investments (10%) while maintaining a more balanced approach with 85% equities and 5% bonds.
            """;
        when(llmService.generateText(anyString())).thenReturn(mockRecommendation);

        String userIntent = "I would like to roll over my stable and boring 401k with into a brokerage account and go all in on crypto";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        assertNotNull(recommendation, "Recommendation should not be null");
        assertTrue(recommendation.length() > 0, "Recommendation should not be empty");
        
        // Should include risk warnings and YOLO portfolio details
        assertTrue(recommendation.contains("risk"), "Should discuss risk");
        assertTrue(recommendation.contains("WARNING"), "Should include risk warnings");
        assertTrue(recommendation.contains("YOLO Portfolio"), "Should mention YOLO portfolio");
        assertTrue(recommendation.contains("100% alternative investments"), "Should describe portfolio composition");
        assertTrue(recommendation.contains("capital loss"), "Should warn about potential losses");
        
        // Should not contain any letter formatting elements
        assertFalse(recommendation.contains("Yours"), "Should not contain closing");
        assertFalse(recommendation.contains("Subject:"), "Should not contain letter header");
        assertFalse(recommendation.contains("Dear"), "Should not contain greeting");
    }

    @Test
    void testRecommendationCleaning() throws IOException {
        // Mock LLM response with unwanted elements
        String mockResponse = """
            Dear Mr. Smith,
            
            Date: January 23, 2024
            
            Based on your request to transfer your pension to Aviva, I recommend the following:
            The transfer to Aviva's platform would be beneficial due to their competitive fee structure.
            
            Best regards,
            Financial Advisor
            """;
        
        when(llmService.generateText(anyString())).thenReturn(mockResponse);
        
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
        assertTrue(recommendation.contains("competitive fee structure"), "Should preserve recommendation details");
    }

    @Test
    void testRecommendationRetry() throws IOException {
        // Mock LLM responses - first with unwanted elements, then clean
        String firstResponse = """
            Dear Client,
            
            I recommend the YOLO Portfolio for your crypto investment needs.
            
            Sincerely,
            Advisor
            """;
            
        String secondResponse = """
            The YOLO Portfolio would align with your interest in cryptocurrency investments. 
            However, I must emphasize that this is an ultra-high-risk strategy that requires careful consideration.
            """;
            
        when(llmService.generateText(anyString()))
            .thenReturn(firstResponse)
            .thenReturn(secondResponse);
            
        String userIntent = "I want to go all in on crypto";
        String recommendation = intentProcessingService.processIntent(userIntent);
        
        // Verify final recommendation is clean and contains important content
        assertFalse(recommendation.contains("Dear"), "Should not contain greeting");
        assertFalse(recommendation.contains("Sincerely"), "Should not contain closing");
        assertTrue(recommendation.contains("YOLO Portfolio"), "Should preserve portfolio name");
        assertTrue(recommendation.contains("ultra-high-risk"), "Should preserve risk warning");
    }
}
