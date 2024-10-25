package com.example.chatApplication;

import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PdfService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";
    private final ConcurrentHashMap<String, String> pdfContentMap = new ConcurrentHashMap<>(); // Store content by filename

    @PostConstruct
    public void init() {
        // Create the upload directory if it does not exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs(); // Create the directory structure
            if (created) {
                System.out.println("Upload directory created at: " + uploadDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create upload directory.");
            }
        } else {
            System.out.println("Upload directory already exists at: " + uploadDir.getAbsolutePath());
        }
    }

    // Method to process the uploaded PDF
    public String processPdf(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename(); // Get the original filename
        File uploadedFile = new File(UPLOAD_DIR + filename);
        file.transferTo(uploadedFile); // Save the uploaded file

        // Extract text from the PDF and store it for later access
        String extractedText = extractTextFromPdf(uploadedFile);
        pdfContentMap.put(filename, extractedText); // Store the extracted content by filename

        return filename; // Return the filename to indicate successful upload
    }

    // Method to extract text using PDFBox
    private String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            if (document.isEncrypted()) {
                throw new IOException("Cannot process encrypted PDF files.");
            }
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document); // Return extracted text
        }
    }

    // Method to get extracted text by filename
    public String getPdfContent(String filename) {
        return pdfContentMap.get(filename); // Return the extracted content
    }

    // Method to answer a question based on PDF content
    public String answerQuestion(String filename, String question) {
        String content = getPdfContent(filename);
        if (content == null) {
            return "No content found for the specified file.";
        }

        // Improved keyword search logic to find relevant information
        // If the content contains the question or a relevant section, return that
        String questionLower = question.toLowerCase();
        if (content.toLowerCase().contains(questionLower)) {
            return extractRelevantSection(content, questionLower); // Return a relevant snippet
        } else {
            return "Sorry, the answer to your question was not found in the PDF content.";
        }
    }

    // Helper method to extract a relevant section of content
    private String extractRelevantSection(String content, String question) {
        // Here you can implement a more sophisticated logic to extract a snippet
        // For demonstration, we'll simply return a portion of the content
        // You could also use regex or other methods for better context extraction
        int startIndex = content.toLowerCase().indexOf(question);
        int endIndex = Math.min(startIndex + 200, content.length()); // Take next 200 characters
        return content.substring(startIndex, endIndex); // Return the snippet as the answer
    }
}
