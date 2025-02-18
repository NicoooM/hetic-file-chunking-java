package com.hetic;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws IOException {
        String path = (String) FileReader.load("text.txt");

        List<byte[]> chunks = SimpleCDC.chunkFile(Paths.get(path));

        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Chunk " + (i + 1) + ": " + new String(chunks.get(i)));
        }
    }
}
