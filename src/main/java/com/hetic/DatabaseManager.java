package com.hetic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.github.luben.zstd.Zstd;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:chunks.db";

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

    public static List<byte[]> getAllChunksOrdered() {
        List<byte[]> chunks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT data FROM chunks ORDER BY id ASC");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                byte[] compressedData = rs.getBytes("data");
                chunks.add(decompressChunk(compressedData));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chunks;
    }

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

    private static byte[] decompressChunk(byte[] compressedData) {
        return Zstd.decompress(compressedData, compressedData.length * 5); // Adjust size if needed
    }
}
