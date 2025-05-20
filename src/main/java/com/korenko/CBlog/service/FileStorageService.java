package com.korenko.CBlog.service;

import com.korenko.CBlog.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig config) {
        this.fileStorageLocation = config.fileStorageLocation();
    }

    public String storeFile(MultipartFile file, Integer id) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = filename.substring(filename.lastIndexOf("."));
        String newFilename = "avatar_" + id + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить файл " + filename, ex);
        }
    }

    public Resource loadFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Файл не найден " + filename, ex);
        }
    }
}
