package com.hetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleCDC {
    private static final int MIN_CHUNK_SIZE = 64;
    private static final int MAX_CHUNK_SIZE = 128;

    // Parameters for Rabin Fingerprinting
    private static final int WINDOW_SIZE = 48;  // window length for rolling hash
    private static final int MASK = 0x0FFF;       // mask to consider 12 bits
    private static final int TARGET = 0x000;      // condition: 12 bits de tête à 0

    public static List<byte[]> chunkFile(Path filePath) throws IOException {
        byte[] data = Files.readAllBytes(filePath);
        List<byte[]> chunks = new ArrayList<>();
        
        int lastCut = 0;
        int fingerprint = 0;
        for (int i = 0; i < data.length; i++) {
            fingerprint = (fingerprint << 1) + Byte.toUnsignedInt(data[i]);

            if (i - lastCut >= WINDOW_SIZE) {
                fingerprint -= (Byte.toUnsignedInt(data[i - WINDOW_SIZE]) << WINDOW_SIZE);
            }

        
            if (i - lastCut + 1 >= MIN_CHUNK_SIZE) {
                if ((fingerprint & MASK) == TARGET || (i - lastCut + 1) >= MAX_CHUNK_SIZE) {
                    chunks.add(Arrays.copyOfRange(data, lastCut, i + 1));
                    lastCut = i + 1;
                    fingerprint = 0; // Reset the fingerprint after making a cut
                }
            }
        }

        // Ajouter le dernier chunk si nécessaire
        if (lastCut < data.length) {
            chunks.add(Arrays.copyOfRange(data, lastCut, data.length));
        }

        return chunks;
    }
}
