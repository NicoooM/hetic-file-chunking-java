package com.hetic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    // Returns a path string, either from the file system or from the JAR
    public static Object load(String fileName, String extension) {
        try {
            // Try loading as a resource in JAR (for packaged mode)
            InputStream is = FileReader.class.getClassLoader().getResourceAsStream(fileName);
            if (is != null) {
                // Handle the case when the file is inside the JAR file
                Path tempFile = Files.createTempFile("temp_", extension);
                Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return tempFile.toString();  // Return the path to the temporary file
            } else {
                // If the resource wasn't found in the JAR, assume it's a file path on the local file system
                Path filePath = Paths.get(fileName);
                if (Files.exists(filePath)) {
                    return filePath.toString();
                } else {
                    throw new IOException("File not found: " + fileName);
                }
            }
        } catch (IOException e) {
            return e;  // Return the exception to handle it in App
        }
    }
}
