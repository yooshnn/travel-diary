package com.td.traveldiary.global.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/*
 * LocalFileStorageService 테스트.
 *
 * 파일시스템에 직접 의존하는 테스트다. @TempDir로 테스트용 임시 디렉토리를 생성하고
 * 테스트 종료 후 자동으로 정리한다.
 *
 * Java 표준 라이브러리(Files.copy, Files.deleteIfExists)의 동작을 검증하는 것이 아니라
 * UUID 파일명 생성, 확장자 보존, URL 반환 형식, 파일 존재 여부 등
 * 이 클래스 고유의 로직을 검증하는 것이 목적이다.
 */
class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new LocalFileStorageService(
                tempDir.toString(),
                "http://localhost:8080/uploads"
        );
    }

    @Test
    void store_saves_file_and_returns_url() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test content".getBytes());

        String url = storageService.store(file);

        assertThat(url).startsWith("http://localhost:8080/uploads/");
        // URL에서 파일명 추출 후 실제 파일 존재 확인
        String filename = url.substring(url.lastIndexOf("/") + 1);
        assertThat(Files.exists(tempDir.resolve(filename))).isTrue();
    }

    @Test
    void store_generates_unique_filename() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test content".getBytes());

        String url1 = storageService.store(file);
        String url2 = storageService.store(file);

        assertThat(url1).isNotEqualTo(url2);
    }

    @Test
    void store_preserves_file_extension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "test content".getBytes());

        String url = storageService.store(file);

        assertThat(url).endsWith(".png");
    }

    @Test
    void delete_removes_file() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test content".getBytes());
        String url = storageService.store(file);
        String filename = url.substring(url.lastIndexOf("/") + 1);

        storageService.delete(url);

        assertThat(Files.exists(tempDir.resolve(filename))).isFalse();
    }

    @Test
    void delete_does_not_throw_when_file_not_exists() {
        // 존재하지 않는 파일 삭제 시 예외 없이 통과
        storageService.delete("http://localhost:8080/uploads/nonexistent.jpg");
    }
}
