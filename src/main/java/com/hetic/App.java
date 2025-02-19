package com.hetic;

import java.nio.file.*;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();

            String path = (String) FileReader.load("static/text.txt", ".txt");
            System.out.println("Path: " + path);
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));

            List<SimpleCDC.Chunk> chunks = SimpleCDC.chunkFile(fileBytes, path.toString());

            for (int i = 0; i < chunks.size(); i++) {
                System.out.println("Chunk " + (i + 1) + ": " +
                        chunks.get(i).data.length + " bytes, Hash: " + chunks.get(i).hash +
                        ", Compressed: " + chunks.get(i).compressedData.length + " bytes");
            }

            // Reconstruct file
            Path outputPath = Paths.get("reconstructed_text.txt");
            Reconstructor.reconstructFile(outputPath);

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
