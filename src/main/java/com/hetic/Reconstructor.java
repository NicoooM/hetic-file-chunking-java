package com.hetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Reconstructor {
    public static void reconstructFile(Path outputPath) {
        try {
            List<byte[]> chunks = DatabaseManager.getAllChunksOrdered();
            Files.write(outputPath, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (byte[] chunk : chunks) {
                Files.write(outputPath, chunk, StandardOpenOption.APPEND);
            }
            System.out.println("File successfully reconstructed at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
