package com.hetic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileCuttingTimeTest {
    
    @Test
    public void testFileCuttingPerformance() {
        try {
            DatabaseManager.initializeDatabase();
            
            String path = (String) FileReader.load("static/naza.jpeg", ".jpeg");
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            
            long startTime = System.currentTimeMillis();
            
            int fileId = SimpleCDC.chunkFile(fileBytes, path);
            
            long endTime = System.currentTimeMillis();
            
            long duration = endTime - startTime;
            System.out.println("[FileCuttingTime Test] - Time: " + duration + " ms");
            System.out.println("[FileCuttingTime Test] - Size: " + fileBytes.length + " bytes");
            
            assertTrue(fileId > 0, "[FileCuttingTime Test] - Success!");
            
        } catch (Exception e) {
            fail("[FileCuttingTime Test] - Error: " + e.getMessage());
        }
    }
}
