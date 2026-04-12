package com.td.traveldiary.global.annotation;

import com.td.traveldiary.global.validator.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ValidImageFile {
    String message() default "유효하지 않은 이미지 파일입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSizeMb() default 5;         // 기본 5MB. 0이면 용량 제한 없음
    String[] allowedTypes() default {}; // 빈 배열이면 image/* 전체 허용
}
