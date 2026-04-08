package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

class PageResponseTest {

    @Test
    void totalPages는_올림_계산된다() {}

    @Test
    void 첫_페이지는_hasPrev가_false다() {}

    @Test
    void 마지막_페이지는_hasNext가_false다() {}

    @Test
    void 중간_페이지는_hasPrev와_hasNext가_모두_true다() {}

    @Test
    void map은_content_타입을_변환하고_메타데이터는_유지된다() {}
}
