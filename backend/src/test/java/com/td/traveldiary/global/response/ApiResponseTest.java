package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void onSuccess는_success가_true이고_data가_담긴다() {}

    @Test
    void onSuccess에_null을_넘기면_data가_null이다() {}

    @Test
    void onFailure는_success가_false이고_message가_담긴다() {}
}
