package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

class SliceResponseTest {

    record TestCursor(Long lastId) implements SliceResponse.Cursor {}

    @Test
    void content가_비어있으면_cursor가_null이다() {}

    @Test
    void cursor는_마지막_아이템에서_추출된다() {}

    @Test
    void map은_content_타입을_변환하고_메타데이터는_유지된다() {}

    @Test
    void content가_비어있지_않으면_cursor는_항상_추출된다() {}
}
