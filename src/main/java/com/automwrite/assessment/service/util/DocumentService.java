package com.automwrite.assessment.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class DocumentService {

    private static final String RECOMMENDATION_MARKER = "// TODO INSERT ADVICE HERE";

    /**
     * Saves the provided XWPFDocument to a specified file name.
     *
     * @param document The XWPFDocument to be saved
     * @throws IOException If an error occurs during the file writing process
     */
    public void saveDocument(XWPFDocument document) throws IOException {
        try (FileOutputStream out = new FileOutputStream("recommendation.docx")) {
            document.write(out);
        }
    }

    /**
     * Loads a predefined template from the resources folder.
     *
     * @return The loaded XWPFDocument template
     * @throws IOException If the template file is not found or cannot be loaded
     */
    public XWPFDocument loadTemplate() throws IOException {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/recommendation-template.docx")) {
            if (templateStream == null) {
                throw new IOException("Template file not found");
            }
            return new XWPFDocument(templateStream);
        }
    }

    /**
     * Inserts the recommendation text into the template at the marked location.
     *
     * @param document The template document
     * @param recommendation The recommendation text to insert
     */
    public void insertRecommendation(XWPFDocument document, String recommendation) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text != null && text.contains(RECOMMENDATION_MARKER)) {
                // Clear the existing paragraph content
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    for (int i = runs.size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }
                }
                
                // Add the recommendation text
                XWPFRun run = paragraph.createRun();
                run.setText(recommendation);
                run.addBreak();  // Add a line break after the recommendation
                
                log.debug("Inserted recommendation at marker");
                return;
            }
        }
        log.warn("Recommendation marker not found in template");
    }
}
