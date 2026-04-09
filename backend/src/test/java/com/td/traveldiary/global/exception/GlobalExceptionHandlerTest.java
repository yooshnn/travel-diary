package com.td.traveldiary.global.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

/*
 * # @WebMvcTest
 * Spring MVC 레이어만 로드하는 슬라이스 테스트다.
 * DB, Service 등 전체 컨텍스트를 띄우지 않아 @SpringBootTest보다 가볍다.
 * controllers = TestController.class 로 테스트 대상 컨트롤러를 명시적으로 제한한다.
 * @ControllerAdvice로 정의된 예외 처리 bean도 함께 등록되어,
 * 컨트롤러에서 발생한 예외를 GlobalExceptionHandler가 처리할 수 있다.
 *
 * # @AutoConfigureMockMvc(addFilters = false)
 * Security Filter Chain을 포함한 모든 필터를 비활성화한다.
 * 이 테스트는 GlobalExceptionHandler 동작만 검증하므로 인증/인가는 제외한다.
 *
 * # TestController
 * 예외를 강제로 발생시키는 테스트 전용 컨트롤러다.
 *
 * # MockMvc
 * 실제 서버를 띄우지 않고 HTTP 요청/응답을 시뮬레이션한다.
 * @WebMvcTest가 자동으로 빈으로 등록하며 @Autowired로 주입받아 사용한다.
 *
 * # 검증 대상
 * - BusinessException → ErrorCode의 httpStatus + ApiResponse 실패 응답으로 변환
 * - MethodArgumentNotValidException → 400
 * - 그 외 예외 → 500
 */
@WebMvcTest(controllers = TestController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void business_exception_is_converted_to_api_response_failure() throws Exception {
        mockMvc.perform(get("/test/business-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }

    @Test
    void method_argument_not_valid_exception_returns_400() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void unhandled_exception_returns_500() throws Exception {
        mockMvc.perform(get("/test/unhandled-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
}
