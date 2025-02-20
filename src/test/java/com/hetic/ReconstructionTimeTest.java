package com.hetic;

import org.junit.jupiter.api.Test;
import java.nio.file.*;

public class ReconstructionTimeTest {
    
    @Test
    public void testReconstructionTime() throws Exception {
        DatabaseManager.initializeDatabase();

        String path = (String) FileReader.load("static/naza.jpeg", ".jpeg");
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        
        System.out.println("[ReconstructionTime Test] Original size: " + fileBytes.length + " bytes");
        
        long startChunking = System.currentTimeMillis();
        int fileId = SimpleCDC.chunkFile(fileBytes, path);
        long chunkingTime = System.currentTimeMillis() - startChunking;
        
        System.out.println("[ReconstructionTime Test] Cutting time: " + chunkingTime + " ms");

        Path outputPath = Paths.get("naza_reconstructed.jpeg");
        long startReconstruction = System.currentTimeMillis();
        Reconstructor.reconstructFile(outputPath, fileId);
        long reconstructionTime = System.currentTimeMillis() - startReconstruction;

        System.out.println("[ReconstructionTime Test] Reconstruction time: " + reconstructionTime + " ms");
        
        assert Files.exists(outputPath) : "[ReconstructionTime Test] Error: File doesn't exist";
        
        byte[] reconstructedBytes = Files.readAllBytes(outputPath);
        assert reconstructedBytes.length == fileBytes.length : "[ReconstructionTime Test] Error: Same size";
        
        Files.deleteIfExists(outputPath);
    }
}
