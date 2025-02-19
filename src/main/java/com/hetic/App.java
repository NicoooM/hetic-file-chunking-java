package com.hetic;

import java.nio.file.*;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();

            String path = (String) FileReader.load("static/girafe.avif", ".avif");
            System.out.println("Path: " + path);
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));

            int fileId = SimpleCDC.chunkFile(fileBytes, path.toString());

            // Reconstruct file
            Path outputPath = Paths.get("reconstructed.avif");
            Reconstructor.reconstructFile(outputPath, fileId);

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
