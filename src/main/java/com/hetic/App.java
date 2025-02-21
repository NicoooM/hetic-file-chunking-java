package com.hetic;

import java.nio.file.*;

public class App {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java App <filename> <extension>");
            System.out.println("Example: java App girafe .avif");
            return;
        }

        String filename = args[0];
        String extension = args[1];

        try {
            DatabaseManager.initializeDatabase();

            String path = (String) FileReader.load("static/" + filename + extension, extension);
            System.out.println("Processing file: " + path);
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));

            int fileId = SimpleCDC.chunkFile(fileBytes, path);

            // Reconstruct file
            Path outputPath = Paths.get("reconstructed" + extension);
            Reconstructor.reconstructFile(outputPath, fileId);
            System.out.println("File reconstructed successfully: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
