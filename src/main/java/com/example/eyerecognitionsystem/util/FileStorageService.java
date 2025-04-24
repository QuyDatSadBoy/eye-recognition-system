package com.example.eyerecognitionsystem.util;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStoragePath;
    private Path eyeImagePath;
    private Path faceImagePath;

    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.eyeImagePath = Paths.get(uploadDir + "/eyes").toAbsolutePath().normalize();
        this.faceImagePath = Paths.get(uploadDir + "/faces").toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStoragePath);
            Files.createDirectories(eyeImagePath);
            Files.createDirectories(faceImagePath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file", e);
        }
    }

    public String storeFile(MultipartFile file, String directory) {
        try {
            // Kiểm tra tên file
            if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                throw new RuntimeException("Tên file không hợp lệ");
            }

            // Tạo tên file ngẫu nhiên để tránh trùng lặp
            String fileExtension = "";
            String originalFileName = file.getOriginalFilename();
            int lastIndex = originalFileName.lastIndexOf('.');
            if (lastIndex > 0) {
                fileExtension = originalFileName.substring(lastIndex);
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Xác định đường dẫn lưu file
            Path targetPath;
            String relativePath;

            if ("eyes".equals(directory)) {
                targetPath = eyeImagePath.resolve(fileName);
                relativePath = "/uploads/eyes/" + fileName;
            } else if ("faces".equals(directory)) {
                targetPath = faceImagePath.resolve(fileName);
                relativePath = "/uploads/faces/" + fileName;
            } else {
                targetPath = fileStoragePath.resolve(fileName);
                relativePath = "/uploads/" + fileName;
            }

            // Copy file vào thư mục đích
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + e.getMessage(), e);
        }
    }

    public Path getFilePath(String relativePath) {
        if (relativePath.startsWith("/uploads/")) {
            relativePath = relativePath.substring("/uploads/".length());
        }

        if (relativePath.startsWith("eyes/")) {
            return eyeImagePath.resolve(relativePath.substring("eyes/".length()));
        } else if (relativePath.startsWith("faces/")) {
            return faceImagePath.resolve(relativePath.substring("faces/".length()));
        } else {
            return fileStoragePath.resolve(relativePath);
        }
    }
}