package com.hetic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.github.luben.zstd.Zstd;

// Gestion SQLite pour stockage chunks dédupliqués
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chunks.db";

    // Init schema DB: files, chunks et mapping
    public static void initializeDatabase() {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         Statement stmt = conn.createStatement()) {
        stmt.execute("CREATE TABLE IF NOT EXISTS files (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name TEXT)");

        stmt.execute("CREATE TABLE IF NOT EXISTS chunks (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "hash TEXT UNIQUE, " +
                     "data BLOB)");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS files_chunks (" +
                     "file_id INTEGER, " +
                     "chunk_id INTEGER, " +
                     "FOREIGN KEY (file_id) REFERENCES files(id), " +
                     "FOREIGN KEY (chunk_id) REFERENCES chunks(id), " +
                     "PRIMARY KEY (file_id, chunk_id))");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Vérifie existence chunk par hash
    public static boolean chunkExists(String hash) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM chunks WHERE hash = ?")) {
            stmt.setString(1, hash);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Enregistre métadonnées fichier
    public static int storeFile(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO files (name) VALUES (?)")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            return stmt.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Stocke nouveau chunk si non existant
    public static int storeChunk(String hash, byte[] compressedData) {
        if (!chunkExists(hash)) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO chunks (hash, data) VALUES (?, ?)")) {
                stmt.setString(1, hash);
                stmt.setBytes(2, compressedData);
                stmt.executeUpdate();
                return stmt.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    // Associe chunk à fichier
    public static void storeFileChunk(int fileId, int chunkId) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO files_chunks (file_id, chunk_id) VALUES (?, ?)")) {
            stmt.setInt(1, fileId);
            stmt.setInt(2, chunkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère chunks ordonnés d'un fichier
    public static List<byte[]> getAllChunksOrdered(int fileId) {
        List<byte[]> chunks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = conn.prepareStatement("SELECT chunks.data FROM chunks " +
                                                        "JOIN files_chunks ON chunks.id = files_chunks.chunk_id " +
                                                        "WHERE files_chunks.file_id = ? " +
                                                        "ORDER BY files_chunks.chunk_id")) {
            stmt.setInt(1, fileId);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                byte[] compressedData = rs.getBytes("data");
                chunks.add(decompressChunk(compressedData));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chunks;
    }

    // Retrouve ID chunk par hash
    public static int getChunkIdByHash(String hash) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM chunks WHERE hash = ?")) {
            stmt.setString(1, hash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Décompression Zstd des chunks
    private static byte[] decompressChunk(byte[] compressedData) {
        return Zstd.decompress(compressedData, compressedData.length * 2);
    }
}
