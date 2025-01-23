package com.automwrite.assessment.service;

import com.automwrite.assessment.model.client.ClientData;
import com.automwrite.assessment.model.organization.OrganizationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service responsible for processing user intents and generating recommendations using LLM.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentProcessingService {

    private final LlmService llmService;
    private final JsonParserService jsonParserService;

    // Common letter elements that should not be in the recommendation
    private static final List<String> UNWANTED_ELEMENTS = Arrays.asList(
        "Dear", "Sincerely", "Best regards", "Yours", "Date:","[Date]", "Signature",
        "Re:", "Subject:", "To:", "From:"
    );

    // Patterns that indicate letter formatting
    private static final List<Pattern> UNWANTED_PATTERNS = Arrays.asList(
        Pattern.compile("^[A-Za-z]+ \\d{1,2},? \\d{4}"), // Date patterns like "January 23, 2024"
        Pattern.compile("(?i)dear .+"), // Dear followed by anything
        Pattern.compile("(?i)sincerely,?.*$", Pattern.MULTILINE), // Closing statements
        Pattern.compile("(?i)regards,?.*$", Pattern.MULTILINE) // Closing statements
    );

    /**
     * Process the user intent and generate a recommendation.
     *
     * @param userIntent The raw user intent text
     * @return Generated recommendation text
     */
    public String processIntent(String userIntent) throws IOException {
        ClientData clientData = jsonParserService.loadClientData();
        OrganizationData orgData = jsonParserService.loadOrganizationData();

        String recommendation = generateRecommendation(userIntent, clientData, orgData);
        
        // Validate and retry if necessary
        int attempts = 0;
        while (containsUnwantedElements(recommendation) && attempts < 3) {
            log.warn("Recommendation contains unwanted elements, retrying. Attempt: {}", attempts + 1);
            log.debug("Original response:\n{}", recommendation);
            
            String retryPrompt = buildRetryPrompt(userIntent, clientData, orgData, recommendation);
            recommendation = llmService.generateText(retryPrompt);
            log.debug("Retry response:\n{}", recommendation);
            
            attempts++;
        }

        if (containsUnwantedElements(recommendation)) {
            log.warn("Failed to generate clean recommendation after {} attempts", attempts);
            // Clean up the recommendation as a last resort
            recommendation = cleanRecommendation(recommendation);
        }

        return recommendation;
    }

    private String generateRecommendation(String userIntent, ClientData clientData, OrganizationData orgData) {
        String prompt = buildPrompt(userIntent, clientData, orgData);
        return llmService.generateText(prompt);
    }

    private boolean containsUnwantedElements(String text) {
        // Check for unwanted text elements
        for (String element : UNWANTED_ELEMENTS) {
            if (text.contains(element)) {
                log.debug("Found unwanted element: {}", element);
                return true;
            }
        }

        // Check for unwanted patterns
        for (Pattern pattern : UNWANTED_PATTERNS) {
            if (pattern.matcher(text).find()) {
                log.debug("Found unwanted pattern: {}", pattern.pattern());
                return true;
            }
        }

        return false;
    }

    private String cleanRecommendation(String text) {
        String cleaned = text;

        // Remove any lines containing unwanted elements
        for (String element : UNWANTED_ELEMENTS) {
            cleaned = Arrays.stream(cleaned.split("\n"))
                .filter(line -> !line.contains(element))
                .reduce((a, b) -> a + "\n" + b)
                .orElse(cleaned);
        }

        // Remove lines matching unwanted patterns
        for (Pattern pattern : UNWANTED_PATTERNS) {
            cleaned = Arrays.stream(cleaned.split("\n"))
                .filter(line -> !pattern.matcher(line).find())
                .reduce((a, b) -> a + "\n" + b)
                .orElse(cleaned);
        }

        return cleaned.trim();
    }

    private String buildPrompt(String userIntent, ClientData clientData, OrganizationData orgData) {
        return String.format("""
            Generate ONLY the recommendation section content for a financial advice letter. 
            DO NOT include any letter formatting elements like date, greeting, closing, or signature.
            The content will be inserted into an existing letter template.
            
            Focus on analyzing the following user intent and providing a clear recommendation 
            based on the client's profile and available investment options.
            
            User Intent: %s
            
            Client Profile:
            - Risk Tolerance: %s
            - Investment Horizon: %s
            - Current Assets: £%d liquid, £%d total net worth
            - Current Pension Plans:
              * Guaranteed Plan: £%d with %s
              * Market Based Plan: £%d with %s
            
            Available Investment Options:
            1. Cautious Growth Portfolio (Risk Level 3)
               - 60%% bonds, 30%% equities, 10%% cash
               - Minimum Investment: £25,000
            2. Balanced Growth Portfolio (Risk Level 5)
               - 60%% equities, 35%% bonds, 5%% alternatives
               - Minimum Investment: £50,000
            3. Aggressive Growth Portfolio (Risk Level 8)
               - 85%% equities, 10%% alternatives, 5%% bonds
               - Minimum Investment: £75,000
            4. YOLO Portfolio (Risk Level 11)
               - 100%% alternatives (crypto focus)
               - Minimum Investment: £75,000
            
            Available Platforms:
            1. Aviva Platform
               - Full-service investment platform
               - ISA and Pension wrappers available
            2. Transact
               - Premium platform with advanced trading
               - Alternative investments available
            3. FundsNetwork
               - Cost-effective with extensive fund range
            
            Instructions:
            1. Analyze the user's intent and current financial situation
            2. Consider their risk tolerance and investment horizon
            3. Recommend appropriate portfolio(s) and platform(s)
            4. Include relevant fee information
            5. Add risk warnings if the requested strategy exceeds their risk tolerance
            6. Generate ONLY the recommendation content - no letter formatting
            
            Generate the recommendation content:
            """,
            userIntent,
            clientData.getClientInfo().getFinancialProfile().getRiskTolerance(),
            clientData.getClientInfo().getFinancialProfile().getInvestmentHorizon(),
            clientData.getClientInfo().getFinancialProfile().getLiquidAssets(),
            clientData.getClientInfo().getFinancialProfile().getTotalNetWorth(),
            clientData.getPensionPlans().getGuaranteedPlan().getPlanValue(),
            clientData.getPensionPlans().getGuaranteedPlan().getProvider(),
            clientData.getPensionPlans().getMarketBasedPlan().getPlanValue(),
            clientData.getPensionPlans().getMarketBasedPlan().getProvider()
        );
    }

    private String buildRetryPrompt(String userIntent, ClientData clientData, OrganizationData orgData, String previousResponse) {
        return String.format("""
            Your previous response included letter formatting elements that should not be included.
            Generate ONLY the recommendation content without any letter formatting.
            
            Previous response for reference:
            %s
            
            Instructions:
            1. Remove all letter formatting (date, greeting, closing, signature)
            2. Keep only the core recommendation content
            3. Ensure the content flows naturally within an existing letter template
            
            Generate a clean recommendation section:
            
            %s
            """,
            previousResponse,
            buildPrompt(userIntent, clientData, orgData)
        );
    }
}
