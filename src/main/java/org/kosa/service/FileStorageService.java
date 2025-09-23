package org.kosa.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.FilesUploadConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FilesUploadConfig filesUploadConfig;

    @PostConstruct
    public void init() {
        final String dir = filesUploadConfig.getDir();
        if (dir == null || dir.isBlank()) {
            throw new IllegalStateException(
                    "Missing required property 'file.upload.dir' (env: FILE_UPLOAD_DIR).");
        }
        try {
            Path uploadPath = Path.of(dir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath); // 존재해도 OK
            if (!Files.isWritable(uploadPath)) {
                throw new IllegalStateException("Upload dir not writable: " + uploadPath);
            }
            log.info("업로드 디렉토리 준비 완료: {}", uploadPath);
        } catch (Exception e) { // NPE/InvalidPathException 포함 위해 폭넓게
            throw new IllegalStateException("업로드 디렉토리 초기화 실패", e);
        }
    }


    public String storeFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(extension);

        try {
            Path targetPath = Paths.get(filesUploadConfig.getDir()).resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("파일 저장 완료: {}", targetPath.toAbsolutePath());
            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public Resource loadFile(String filename) {
        try {
            Path filePath = Paths.get(filesUploadConfig.getDir()).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 로드 실패: " + filename, e);
        }
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(filesUploadConfig.getDir()).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filename, e);
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기가 10MB를 초과합니다");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !isAllowedExtension(filename)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다");
        }
    }

    private boolean isAllowedExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList(filesUploadConfig.getAllowedExtensionsArray())
                .contains(extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    public String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
