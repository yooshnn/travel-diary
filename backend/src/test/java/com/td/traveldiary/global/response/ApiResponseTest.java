package com.td.traveldiary.global.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void onSuccess_success_is_true_and_data_is_set() {
        String data = "my-data";
        ApiResponse<String> response = ApiResponse.onSuccess(data);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void onSuccess_allows_null_data() {
        ApiResponse<Void> response = ApiResponse.onSuccess(null);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }

    @Test
    void onFailure_success_is_false_and_message_is_set() {
        ApiResponse<Void> response = ApiResponse.onFailure("error message");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("error message");
        assertThat(response.getData()).isNull();
    }
}
