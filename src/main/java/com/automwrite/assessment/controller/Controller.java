package com.automwrite.assessment.controller;

import com.automwrite.assessment.service.JsonParserService;
import com.automwrite.assessment.model.client.ClientData;
import com.automwrite.assessment.model.organization.OrganizationData;
import com.automwrite.assessment.service.IntentProcessingService;
import com.automwrite.assessment.service.LlmService;
import com.automwrite.assessment.service.util.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.automwrite.assessment.service.util.FileParserService.parseTxtFile;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {

    private final IntentProcessingService intentProcessingService;
    private final DocumentService documentService;

    /**
     * Processes the uploaded .txt file to extract user intent, utilises JSON data and an LLM service
     * to process the intent, and generates a .docx file using a predefined template.
     *
     * @param file File to extract the user intent from
     * @return A response indicating that the processing has completed
     * @throws IOException If an error occurs while reading the file or processing the document
     */
    @PostMapping("/user-request")
    public ResponseEntity<String> handleUserRequest(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Empty file provided");
            }

            // Parse the .txt file to extract the user intent
            String userIntent = parseTxtFile(file);
            if (userIntent.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: No user intent found in file");
            }

            try {
                // Process the intent and generate recommendation
                String recommendation = intentProcessingService.processIntent(userIntent);

                // Load the template document
                XWPFDocument templateDocument = documentService.loadTemplate();

                // Insert the recommendation into the template
                documentService.insertRecommendation(templateDocument, recommendation);
                documentService.saveDocument(templateDocument);

                return ResponseEntity.ok("User request processed, recommendation created.");
            } catch (RuntimeException e) {
                log.error("Error processing intent: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error reading file: " + e.getMessage());
        }
    }
}
