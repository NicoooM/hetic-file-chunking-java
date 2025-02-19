package com.hetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

public class SimpleCDC {
    private static final int MIN_CHUNK_SIZE = 64;
    private static final int MAX_CHUNK_SIZE = 128;

    // Parameters for Rabin Fingerprinting
    private static final int WINDOW_SIZE = 48;  // window length for rolling hash
    private static final int MASK = 0x0FFF;      // mask to consider 12 bits
    private static final int TARGET = 0x000;     // condition: 12 bits de tête à 0

    public static List<byte[]> chunkFile(Path filePath) throws IOException {
        byte[] data = Files.readAllBytes(filePath);
        List<byte[]> chunks = new ArrayList<>();
        
        // Initialisation du polynomial et de la fenêtre pour le Rabin Fingerprint
        Polynomial polynomial = Polynomial.createIrreducible(53);
        RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(polynomial, WINDOW_SIZE);

        int lastCut = 0;

        // Itération sur chaque byte du fichier pour générer des chunks
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            // Ajouter le byte dans la fenêtre, ancien bytes sont retirés automatiquement
            window.pushByte(b);

            // Vérification de la condition pour un cut
            if (i - lastCut + 1 >= MIN_CHUNK_SIZE) {
                // Si le fingerprint respecte le masque ou que la taille dépasse la taille maximale
                if ((window.getFingerprintLong() & MASK) == TARGET || (i - lastCut + 1) >= MAX_CHUNK_SIZE) {
                    // Ajouter un chunk et réinitialiser le dernier point de coupure
                    chunks.add(Arrays.copyOfRange(data, lastCut, i + 1));
                    lastCut = i + 1;
                }
            }
        }

        // Ajouter le dernier chunk s'il en reste
        if (lastCut < data.length) {
            chunks.add(Arrays.copyOfRange(data, lastCut, data.length));
        }

        return chunks;
    }
}
