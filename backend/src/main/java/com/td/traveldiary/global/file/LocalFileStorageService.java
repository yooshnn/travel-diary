package com.td.traveldiary.global.file;

import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@Profile("local")
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadDir;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${storage.local.path}") String uploadPath,
            @Value("${storage.local.base-url}") String baseUrl) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
    }

    @Override
    public String store(MultipartFile file) {
        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL);
        }

        return baseUrl + "/" + filename;
    }

    @Override
    public void delete(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        try {
            Files.deleteIfExists(uploadDir.resolve(filename));
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filename);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
