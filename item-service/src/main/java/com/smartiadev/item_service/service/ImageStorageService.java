package com.smartiadev.item_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {

    List<String> uploadImages(List<MultipartFile> files);

}
