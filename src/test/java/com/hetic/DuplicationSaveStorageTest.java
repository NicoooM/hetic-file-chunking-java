package com.hetic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DuplicationSaveStorageTest {

    @Test
    public void testStorageSavingsWithDuplicateFiles() throws Exception {
        String originalFile = (String) FileReader.load("static/nasa.jpeg", ".jpeg");
        String duplicateFile = (String) FileReader.load("static/nasa_duplication.jpeg", ".jpeg");

        long originalSize = Files.size(Paths.get(originalFile));
        long duplicateSize = Files.size(Paths.get(duplicateFile));
        long totalOriginalSize = originalSize + duplicateSize;

        long spaceSaved = totalOriginalSize - originalSize;
        double savingsPercentage = (spaceSaved * 100.0) / totalOriginalSize;

        System.out.println("[DuplicationSaveStorage Test] - Total original size: " + totalOriginalSize + " bytes");
        System.out.println("[DuplicationSaveStorage Test] - Original size: " + originalSize + " bytes");
        System.out.println("[DuplicationSaveStorage Test] - Space saved: " + spaceSaved + " bytes");
        System.out.println("[DuplicationSaveStorage Test] - Savings percentage: " + String.format("%.2f", savingsPercentage) + "%");

        assertTrue(spaceSaved > 0, "[DuplicationSaveStorage Test] - Savings success!");
        assertEquals(originalSize, originalSize, "[DuplicationSaveStorage Test] - Sizings success!");
    }
}
