package com.hetic;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            // Load the file path
            String path = (String) FileReader.load("static/text.txt");

            // print the path
            System.out.println("Path: " + path);

            // Use the path with SimpleCDC
            List<byte[]> chunks = SimpleCDC.chunkFile(Paths.get(path));
            
            for (int i = 0; i < chunks.size(); i++) {
                System.out.println("Chunk " + (i + 1) + ": " + chunks.get(i).length + " bytes");
            }
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


