package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * hasPrev, hasNext 테스트를 분리하지 않은 이유:
 * 1. 계산 수식이 단순해 실패할 가능성이 낮다.
 * 2. 실패해도 원인 파악이 어렵지 않다고 생각한다.
 *
 * totalElements=0일 때 totalPages=0:
 * Ceil(0 / size) = 0으로 자연스럽게 계산된다.
 * totalPages=1도 고려했으나 기존 프로젝트의 구현에서 변경해서 얻는 실익이 없다고 판단했다.
 */

class PageResponseTest {

    @Test
    void totalPages_is_calculated_with_ceiling() {
        PageResponse<String> response = PageResponse.of(List.of("a"), 0, 10, 101L);

        assertThat(response.totalPages()).isEqualTo(11);
    }

    @Test
    void of_calculates_pagination_metadata_correctly() {
        PageResponse<String> first = PageResponse.of(List.of("a"), 0, 10, 100L);
        PageResponse<String> middle = PageResponse.of(List.of("a"), 5, 10, 100L);
        PageResponse<String> last = PageResponse.of(List.of("a"), 9, 10, 100L);
        PageResponse<String> single = PageResponse.of(List.of("a"), 0, 1, 1L);

        assertThat(first.hasPrev()).isFalse();
        assertThat(first.hasNext()).isTrue();

        assertThat(middle.hasPrev()).isTrue();
        assertThat(middle.hasNext()).isTrue();

        assertThat(last.hasPrev()).isTrue();
        assertThat(last.hasNext()).isFalse();

        assertThat(single.hasPrev()).isFalse();
        assertThat(single.hasNext()).isFalse();
    }

    @Test
    void map_transforms_content_and_preserves_metadata() {
        PageResponse<String> response = PageResponse.of(List.of("a", "aaa"), 0, 10, 2L);
        PageResponse<Integer> mapped = response.map(String::length);

        assertThat(mapped.content()).containsExactly(1, 3);
        assertThat(mapped.currentPage()).isEqualTo(response.currentPage());
        assertThat(mapped.totalPages()).isEqualTo(response.totalPages());
        assertThat(mapped.totalElements()).isEqualTo(response.totalElements());
    }

    @Test
    void totalPages_is_zero_when_no_elements() {
        PageResponse<String> response = PageResponse.of(List.of(), 0, 10, 0L);

        assertThat(response.totalPages()).isEqualTo(0);
    }
}
