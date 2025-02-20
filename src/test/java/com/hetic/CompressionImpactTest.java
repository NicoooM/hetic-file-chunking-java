package com.hetic;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class CompressionImpactTest {

    @Test
    public void testCompressionPerformance() throws IOException {
        String path = (String) FileReader.load("static/naza.jpeg", ".jpeg");
        byte[] fileBytes = Files.readAllBytes(Paths.get(path));
        
        long startTimeNoCompression = System.currentTimeMillis();
        int originalSize = fileBytes.length;
        long endTimeNoCompression = System.currentTimeMillis();
        
        long startTimeCompression = System.currentTimeMillis();
        byte[] compressedData = SimpleCDC.compressChunk(fileBytes);
        int compressedSize = compressedData.length;
        long endTimeCompression = System.currentTimeMillis();
        
        long timeWithoutCompression = endTimeNoCompression - startTimeNoCompression;
        long timeWithCompression = endTimeCompression - startTimeCompression;
        double compressionRatio = (double) compressedSize / originalSize;
        double spaceSaved = (1 - compressionRatio) * 100;
        
        System.out.println("[CompressionImpact Test] - Original size: " + originalSize + " bytes");
        System.out.println("[CompressionImpact Test] - Compressed size: " + compressedSize + " bytes");
        System.out.println("[CompressionImpact Test] - Compression ratio: " + String.format("%.2f", compressionRatio));
        System.out.println("[CompressionImpact Test] - Savings space: " + String.format("%.2f", spaceSaved) + "%");
        System.out.println("[CompressionImpact Test] - Without compression time: " + timeWithoutCompression + " ms");
        System.out.println("[CompressionImpact Test] - Compression time: " + timeWithCompression + " ms");
        System.out.println("[CompressionImpact Test] - Delta tima: " + (timeWithCompression - timeWithoutCompression) + " ms");
    }
}
