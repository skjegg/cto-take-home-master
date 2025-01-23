package com.automwrite.assessment.service.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class FileParserService {

    private static final String EXPECTED_CONTENT_TYPE = "text/plain";

    /**
     * Parses the content of a .txt file uploaded as a MultipartFile.
     *
     * @param file The uploaded .txt file
     * @return The content of the file as a String
     * @throws IOException If an error occurs while reading the file or if the content type is invalid
     */
    public static String parseTxtFile(MultipartFile file) throws IOException {
        if (!EXPECTED_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IOException("Invalid content type. Expected text/plain but got: " + file.getContentType());
        }
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IOException("Error reading file content: " + e.getMessage(), e);
        }
    }
}
