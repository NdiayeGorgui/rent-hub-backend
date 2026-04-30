package com.smartiadev.messaging_service.service;


import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String uploadImage(MultipartFile file); // ← une seule image pour le chat
}