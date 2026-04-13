package com.td.traveldiary.global.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageFileValidatorTest {

    private ImageFileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ImageFileValidator();
    }

    private void init(long maxSizeMb, String... allowedTypes) {
        ValidImageFile annotation = mock(ValidImageFile.class);
        when(annotation.maxSizeMb()).thenReturn(maxSizeMb);
        when(annotation.allowedTypes()).thenReturn(allowedTypes);
        validator.initialize(annotation);
    }

    @Test
    void null_file_passes() {
        init(5);
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void empty_file_passes() {
        init(5);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    void image_file_passes() {
        init(5);
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[100]);
        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    void non_image_file_fails() {
        init(5);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[100]);
        assertThat(validator.isValid(file, null)).isFalse();
    }

    @Test
    void file_exceeding_max_size_fails() {
        init(1); // 1MB 제한
        byte[] largeContent = new byte[2 * 1024 * 1024]; // 2MB
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", largeContent);
        assertThat(validator.isValid(file, null)).isFalse();
    }

    @Test
    void file_within_max_size_passes() {
        init(5); // 5MB 제한
        byte[] content = new byte[1024]; // 1KB
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", content);
        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    void allowed_type_passes_when_types_specified() {
        init(5, "image/jpeg", "image/png");
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[100]);
        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    void disallowed_type_fails_when_types_specified() {
        init(5, "image/jpeg", "image/png");
        MockMultipartFile file = new MockMultipartFile("file", "photo.gif", "image/gif", new byte[100]);
        assertThat(validator.isValid(file, null)).isFalse();
    }

    @Test
    void any_image_type_passes_when_no_types_specified() {
        init(5); // allowedTypes 비어있음
        MockMultipartFile file = new MockMultipartFile("file", "photo.gif", "image/gif", new byte[100]);
        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    void zero_max_size_allows_any_size() {
        init(0); // 용량 제한 없음
        byte[] largeContent = new byte[100 * 1024 * 1024]; // 100MB
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", largeContent);
        assertThat(validator.isValid(file, null)).isTrue();
    }
}