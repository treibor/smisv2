package com.smis.dbservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${app.storage.base-dir}") String baseDir) {
        this.baseDir = Paths.get(baseDir)
                .toAbsolutePath()
                .normalize();
        //System.out.println("FILES STORED IN: " + this.baseDir);
    }
    public boolean exists(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return false;
        }
        try {
            Path path = resolveSafe(storagePath);
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }
    public String save(InputStream inputStream, String originalFileName) throws IOException {
        Files.createDirectories(baseDir);

        // This name is already unique if you used generateSafeFileName(...)
        String safeName = sanitizeFileName(originalFileName);

        Path target = resolveSafe(safeName); // ensures no path traversal + stays inside baseDir

        try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
            inputStream.transferTo(out);
        }

        // store ONLY this value in DB
        return safeName;
    }

    public InputStream open(String storagePath) throws IOException {
        Path path = resolveSafe(storagePath);
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    public void delete(String storagePath) throws IOException {
        Path path = resolveSafe(storagePath);
        Files.deleteIfExists(path);
    }

    private Path resolveSafe(String storagePath) {
        Path resolved = baseDir.resolve(storagePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new SecurityException("Invalid storage path");
        }
        return resolved;
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "file";
        }
        String s = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        return s.length() > 150 ? s.substring(0, 150) : s;
    }
    public String generateSafeFileName(String prefix, String originalFileName) {

	    String extension = "";

	    if (originalFileName != null) {
	        int dot = originalFileName.lastIndexOf('.');
	        if (dot > -1 && dot < originalFileName.length() - 1) {
	            extension = originalFileName.substring(dot);
	        }
	    }

	    String timestamp = LocalDateTime.now()
	            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

	    return prefix + "_" + timestamp + "_" + UUID.randomUUID() + extension;
	}
    public Path resolveForServing(String storagePath) {
        return resolveSafe(storagePath);
    }
}