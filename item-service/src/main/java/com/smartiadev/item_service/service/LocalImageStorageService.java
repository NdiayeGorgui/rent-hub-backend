package com.smartiadev.item_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalImageStorageService implements ImageStorageService {

    private final String uploadDir;

    public LocalImageStorageService(
            @Value("${UPLOAD_DIR:uploads/}") String uploadDir
    ) {
        this.uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                urls.add("/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Upload failed");
            }
        }
        return urls;
    }

    @Override
    public void deleteImages(List<String> imageUrls) {
        for (String url : imageUrls) {
            try {
                String filename = url.substring(url.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, filename);
                Files.deleteIfExists(filePath);
                log.info("🗑️ Image supprimée: {}", filePath);
            } catch (IOException e) {
                log.error("Erreur suppression image {}: {}", url, e.getMessage());
            }
        }
    }
}