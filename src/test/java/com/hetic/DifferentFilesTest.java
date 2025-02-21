package com.hetic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.util.Arrays;

public class DifferentFilesTest {

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager.initializeDatabase();
    }

    @Test
    void testImageAvif() throws Exception {
        testFile("girafe", ".avif");
    }

    @Test
    void testImageJpeg() throws Exception {
        testFile("nasa", ".jpeg");
    }

    @Test
    void testTextFile() throws Exception {
        testFile("text", ".txt");
    }

    @Test
    void testCsvFile() throws Exception {
        testFile("username", ".csv");
    }

    private void testFile(String filename, String extension) throws Exception {
        // Load original file
        String originalPath = (String) FileReader.load("static/" + filename + extension, extension);
        byte[] originalBytes = Files.readAllBytes(Paths.get(originalPath));

        // Process file
        int fileId = SimpleCDC.chunkFile(originalBytes, originalPath);

        // Reconstruct file
        Path reconstructedPath = Paths.get("reconstructed" + extension);
        Reconstructor.reconstructFile(reconstructedPath, fileId);

        // Compare files
        byte[] reconstructedBytes = Files.readAllBytes(reconstructedPath);
        assertTrue(Arrays.equals(originalBytes, reconstructedBytes),
                "[DifferentFiles Test] - Error: This file are not identical > " + filename + extension);

        // Clean up
        Files.deleteIfExists(reconstructedPath);
    }
}
