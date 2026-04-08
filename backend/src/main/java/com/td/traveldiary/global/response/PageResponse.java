package com.td.traveldiary.global.response;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T> (
    List<T> content,
    int currentPage,
    long totalElements,
    int totalPages,
    int size,
    boolean hasPrev,
    boolean hasNext
) {
    /**
     * hasPrev, hasNext, totalPages는 파생값이므로 호출 측에서 계산하지 않도록 팩토리 메서드로 캡슐화한다.
     *
     * @param content       조회된 데이터 목록
     * @param currentPage   현재 페이지 번호 (1부터 시작)
     * @param size          페이지당 항목 수
     * @param totalElements 전체 데이터 수
     * @return 페이징 메타데이터가 계산된 PageResponse
     */
    public static <T> PageResponse<T> of(List<T> content, int currentPage, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasPrev = currentPage > 1;
        boolean hasNext = currentPage < totalPages;

        return new PageResponse<>(content, currentPage, totalElements, totalPages, size, hasPrev, hasNext);
    }

    /**
     * 페이징 메타데이터는 유지한 채 content 타입만 변환한다.
     * Service → Controller 레이어 간 DTO 변환에 사용한다.
     *
     * @param mapper content 각 항목에 적용할 변환 함수
     * @return 변환된 content를 가진 PageResponse
     */
    public <R> PageResponse<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(mappedContent, currentPage, totalElements, totalPages, size, hasPrev, hasNext);
    }
}
