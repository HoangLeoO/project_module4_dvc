package org.example.project_module4_dvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;

    // Inject đường dẫn từ application.properties
    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ!", e);
        }
    }

    /**
     * 1. Lưu file vật lý
     * 
     * @return String: Đường dẫn tương đối để lưu vào DB (VD: /2023/12/abc.pdf)
     */
    public String store(MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            if (originalFileName.contains("..")) {
                throw new RuntimeException("Tên file không hợp lệ " + originalFileName);
            }

            // Tạo thư mục con theo Năm/Tháng để tránh một thư mục chứa quá nhiều file
            LocalDate now = LocalDate.now();
            String subDir = now.getYear() + "/" + now.getMonthValue(); // VD: 2023/12
            Path targetDir = this.rootLocation.resolve(subDir);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Tạo tên file mới: UUID + Đuôi file gốc
            String fileExtension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                fileExtension = originalFileName.substring(i);
            }
            String storageFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file vào thư mục đích
            Path targetLocation = targetDir.resolve(storageFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối (Web friendly)
            return "/uploads/" + subDir + "/" + storageFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file " + file.getOriginalFilename(), e);
        }
    }

    /**
     * 2. Tải file (Dùng cho chức năng Download/Xem file)
     * 
     * @param fileUrl Đường dẫn tương đối lấy từ DB (VD: /uploads/2023/12/abc.pdf)
     */
    public Resource loadFileAsResource(String fileUrl) {
        try {
            System.out.println("FileStorageService.loadFileAsResource - Input URL: " + fileUrl);

            // 1. Try resolving relative to rootLocation
            String relativePath = fileUrl;
            if (relativePath.startsWith("/uploads/")) {
                relativePath = relativePath.substring(9); // remove "/uploads/"
            } else if (relativePath.startsWith("uploads/")) {
                relativePath = relativePath.substring(8); // remove "uploads/"
            }

            Path filePath = this.rootLocation.resolve(relativePath).normalize();
            System.out.println("Attempt 1 (Relative to rootLocation): " + filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            // 2. Try resolving relative to project Root (if rootLocation is not Root)
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path altPath = projectRoot.resolve(fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl).normalize();
            System.out.println("Attempt 2 (Relative to project root): " + altPath.toAbsolutePath());
            resource = new UrlResource(altPath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            // 3. Try absolute path if it looks like one
            try {
                Path absPath = Paths.get(fileUrl).normalize();
                if (absPath.isAbsolute()) {
                    System.out.println("Attempt 3 (Absolute path): " + absPath.toAbsolutePath());
                    resource = new UrlResource(absPath.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors for Attempt 3
            }

            // If all failed
            System.err.println("FileStorageService - FAILED to load file after all attempts: " + fileUrl);
            throw new RuntimeException("Không thể đọc file: " + fileUrl);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Lỗi đường dẫn file: " + fileUrl, e);
        }
    }
}