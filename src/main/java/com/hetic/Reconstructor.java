package com.hetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

// Classe pour reconstruire les fichiers à partir des chunks stockés
public class Reconstructor {
    // Reconstruit un fichier à partir de ses chunks en les concaténant
    public static void reconstructFile(Path outputPath, int fileId) {
        try {
            // Récupère tous les chunks ordonnés
            List<byte[]> chunks = DatabaseManager.getAllChunksOrdered(fileId);
            // Crée un nouveau fichier vide
            Files.write(outputPath, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Ajoute chaque chunk au fichier
            for (byte[] chunk : chunks) {
                Files.write(outputPath, chunk, StandardOpenOption.APPEND);
            }
            System.out.println("File successfully reconstructed at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
