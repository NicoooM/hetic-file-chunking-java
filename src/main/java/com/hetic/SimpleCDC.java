package com.hetic;

import com.github.luben.zstd.Zstd;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

// Implémentation de Content-Defined Chunking (CDC) avec l'algorithme de Rabin
public class SimpleCDC {
    public static final int MIN_CHUNK_SIZE = 2048;
    public static final int MAX_CHUNK_SIZE = 8192;
    private static final int WINDOW_SIZE = 256;
    private static final int MASK = 0x3FFF;
    private static final int TARGET = 0x1FFF;

    // Classe interne représentant un chunk avec ses données et métadonnées
    public static class Chunk {
        public final byte[] data;
        public final String hash;
        public final byte[] compressedData;

        public Chunk(byte[] data, String hash, byte[] compressedData) {
            this.data = data;
            this.hash = hash;
            this.compressedData = compressedData;
        }
    }

    // Découpe un fichier en chunks en utilisant l'algorithme CDC de Rabin
    public static int chunkFile(byte[] fileBytes, String name) throws IOException, NoSuchAlgorithmException {
        List<Chunk> chunks = new ArrayList<>();

        Polynomial polynomial = Polynomial.createIrreducible(53);
        RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(polynomial, WINDOW_SIZE);

        int lastCut = 0;

        int fileId = DatabaseManager.storeFile(name);

        for (int i = 0; i < fileBytes.length; i++) {
            byte b = fileBytes[i];
            window.pushByte(b);

            if (i - lastCut + 1 >= MIN_CHUNK_SIZE) {
                if ((window.getFingerprintLong() & MASK) == TARGET || (i - lastCut + 1) >= MAX_CHUNK_SIZE) {
                    byte[] chunkData = Arrays.copyOfRange(fileBytes, lastCut, i + 1);
                    String hash = computeHash(chunkData);
                    
                    if (!DatabaseManager.chunkExists(hash)) {
                        byte[] compressedData = compressChunk(chunkData);
                        int chunkId = DatabaseManager.storeChunk(hash, compressedData);
                        DatabaseManager.storeFileChunk(fileId, chunkId);
                        chunks.add(new Chunk(chunkData, hash, compressedData));
                    } else {
                        int chunkId = DatabaseManager.getChunkIdByHash(hash);
                        DatabaseManager.storeFileChunk(fileId, chunkId);
                    }
                    
                    lastCut = i + 1;
                }
            }
        }

        if (lastCut < fileBytes.length) {
            byte[] chunkData = Arrays.copyOfRange(fileBytes, lastCut, fileBytes.length);
            String hash = computeHash(chunkData);

            if (!DatabaseManager.chunkExists(hash)) {
                byte[] compressedData = compressChunk(chunkData);
                int chunkId = DatabaseManager.storeChunk(hash, compressedData);
                DatabaseManager.storeFileChunk(fileId, chunkId);
                chunks.add(new Chunk(chunkData, hash, compressedData));
            } else {
                int chunkId = DatabaseManager.getChunkIdByHash(hash);
                DatabaseManager.storeFileChunk(fileId, chunkId);
            }
        }

        return fileId;
    }

    // Calcule le hash SHA-256 d'un chunk
    public static String computeHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Compresse un chunk avec l'algorithme Zstd
    public static byte[] compressChunk(byte[] data) {
        return Zstd.compress(data);
    }
}
