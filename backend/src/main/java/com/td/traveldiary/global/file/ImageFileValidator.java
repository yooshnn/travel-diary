package com.td.traveldiary.global.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

    private long maxSizeBytes;
    private List<String> allowedTypes;

    @Override
    public void initialize(ValidImageFile annotation) {
        this.maxSizeBytes = annotation.maxSizeMb() * 1024 * 1024;
        this.allowedTypes = Arrays.asList(annotation.allowedTypes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // null이거나 비어있으면 통과 (필수 여부는 @NotNull로 제어한다.)
        if (file == null || file.isEmpty()) return true;

        String contentType = file.getContentType();
        if (contentType == null) return false;

        // image/* 체크
        if (!contentType.startsWith("image/")) return false;

        // allowedTypes가 지정된 경우 추가 체크
        if (!allowedTypes.isEmpty() && !allowedTypes.contains(contentType)) return false;

        // maxSizeMb가 0이면 용량 제한 없음
        if (maxSizeBytes > 0 && file.getSize() > maxSizeBytes) return false;

        return true;
    }
}
