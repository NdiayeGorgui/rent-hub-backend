package com.smartiadev.item_service.service;

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
public class LocalImageStorageService implements ImageStorageService {

    private final String uploadDir = "uploads/";

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
}
