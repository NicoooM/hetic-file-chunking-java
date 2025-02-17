package com.hetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SimpleCDC {
    private static final int WINDOW_SIZE = 2;
    private static final int MASK = 0x0FFF;
    private static final int TARGET = 0x0000;

    public static List<byte[]> chunkFile(Path filePath) throws IOException {
        byte[] data = Files.readAllBytes(filePath);
        List<byte[]> chunks = new ArrayList<>();
        int lastCut = 0;
        int hash = 0;

        for (int i = 0; i < data.length; i++) {
            hash = (hash << 1) + data[i];
            if (i < WINDOW_SIZE) continue;
            hash -= (data[i - WINDOW_SIZE] << WINDOW_SIZE);
            if ((hash & MASK) == TARGET) {
                chunks.add(Arrays.copyOfRange(data, lastCut, i + 1));
                lastCut = i + 1;
            }
            System.out.println(data[i]);
        }
        if (lastCut < data.length) {
            chunks.add(Arrays.copyOfRange(data, lastCut, data.length));
        }
        return chunks;
    }
}
