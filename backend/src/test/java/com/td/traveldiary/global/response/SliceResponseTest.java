package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SliceResponseTest {

    record TestCursor(Long lastId) implements SliceResponse.Cursor {}

    @Test
    void content_is_empty_then_cursor_is_null() {
        SliceResponse<Long, TestCursor> response = SliceResponse.of(
                List.of(),
                false,
                TestCursor::new // = item -> new TestCursor(item)
        );

        assertThat(response.cursor()).isNull();
    }

    @Test
    void cursor_is_extracted_from_last_item() {
        SliceResponse<Long, TestCursor> response = SliceResponse.of(
                List.of(1L, 2L, 3L, 4L),
                true,
                TestCursor::new
        );

        assertThat(response.cursor().lastId()).isEqualTo(4L);
    }

    @Test
    void map_transforms_content_and_preserves_metadata() {
        SliceResponse<Long, TestCursor> response = SliceResponse.of(
                List.of(1L, 2L),
                true,
                TestCursor::new
        );
        SliceResponse<String, TestCursor> mapped = response.map(Object::toString);

        assertThat(mapped.content()).containsExactly("1", "2");
        assertThat(mapped.hasNext()).isEqualTo(response.hasNext());
        assertThat(mapped.cursor()).isEqualTo(response.cursor());
    }

    @Test
    void cursor_is_always_extracted_when_content_is_not_empty() {
        SliceResponse<Long, TestCursor> response = SliceResponse.of(
                List.of(1L, 2L, 3L),
                false,
                TestCursor::new
        );

        assertThat(response.cursor()).isNotNull();
        assertThat(response.cursor().lastId()).isEqualTo(3L);
    }
}
