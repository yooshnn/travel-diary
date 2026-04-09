package com.td.traveldiary.global.response;

import java.util.List;
import java.util.function.Function;

/*
 * # record 사용 이유
 * 생성자를 숨길 필요가 없어 record를 사용했다.
 * of()는 파생값 계산의 편의를 위한 것이지 잘못된 조합 방지가 목적이 아니기 때문이다.
 *
 * # currentPage 0-based
 * JPA Pageable과 맞추고 (page - 1) * size 변환이 여러 곳에 흩어지는 것을 방지한다.
 *
 * # totalPages=0 (totalElements=0인 경우)
 * Math.ceil(0 / size) = 0으로 자연스럽게 계산된다.
 * totalPages=1도 고려했으나 "페이지가 없다"는 의미로 0이 더 직관적이라 판단했다.
 */
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
     */
    public static <T> PageResponse<T> of(List<T> content, int currentPage, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasPrev = currentPage > 0;
        boolean hasNext = currentPage < totalPages - 1;

        return new PageResponse<>(content, currentPage, totalElements, totalPages, size, hasPrev, hasNext);
    }

    /**
     * 페이징 메타데이터는 유지한 채 content 타입만 변환한다.
     * Service → Controller 레이어 간 DTO 변환에 사용한다.
     */
    public <R> PageResponse<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(mappedContent, currentPage, totalElements, totalPages, size, hasPrev, hasNext);
    }
}
