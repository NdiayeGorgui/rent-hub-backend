package com.smartiadev.messaging_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final String uploadDir;

    public LocalImageStorageService(
            @Value("${UPLOAD_DIR_MESSAGES:uploads/messages/}") String uploadDir
    ) {
        this.uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
}