package com.example.chatApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            String filename = pdfService.processPdf(file); // Process the uploaded PDF
            return ResponseEntity.ok("File uploaded successfully: " + filename); // Return success message
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing PDF: " + e.getMessage()); // Return error message
        }
    }

    // New endpoint to ask questions via POST
    @PostMapping("/question")
    public ResponseEntity<String> askQuestion(@RequestBody QuestionRequest request) {
        try {
            String answer = pdfService.answerQuestion(request.getFilename(), request.getQuestion());
            if (answer != null) {
                return ResponseEntity.ok(answer);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No answer found for the specified question.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }



    // Optional method to get the content directly, if needed
    @GetMapping("/content/{filename}")
    public ResponseEntity<String> getPdfContent(@PathVariable String filename) {
        String content = pdfService.getPdfContent(filename); // Fetch the content using filename
        if (content != null) {
            return ResponseEntity.ok(content); // Return the extracted content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No content found for the specified file."); // Handle case where content doesn't exist
        }
    }
}
