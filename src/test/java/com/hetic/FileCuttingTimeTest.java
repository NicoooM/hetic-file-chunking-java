package com.hetic;

import junit.framework.TestCase;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileCuttingTimeTest extends TestCase {
    
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
            
            assertTrue("[FileCuttingTime Test] - Success!", fileId > 0);
            
        } catch (Exception e) {
            fail("[FileCuttingTime Test] - Error: " + e.getMessage());
        }
    }
}
