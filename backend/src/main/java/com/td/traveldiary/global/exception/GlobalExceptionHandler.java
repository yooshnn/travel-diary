package com.td.traveldiary.global.exception;

import com.td.traveldiary.global.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*
 * # @RestControllerAdvice
 * 모든 컨트롤러(@Controller, @RestController)에서 발생하는 예외를 전역적으로 처리한다.
 * @ControllerAdvice + @ResponseBody의 조합이다.
 *
 * # ResponseEntityExceptionHandler 상속
 * Spring MVC가 발생시키는 표준 예외(MethodArgumentNotValidException 등)를 처리하기 위한 기본 베이스 클래스다.
 * 상속받으면 handleMethodArgumentNotValid 같은 메서드를 @Override 하여
 * 기본 예외 처리 방식을 일관되게 재정의할 수 있다.
 * 다만 상속하지 않아도 @ExceptionHandler(MethodArgumentNotValidException.class)로 직접 처리하는 것은 가능하다.
 *
 * # 처리 우선순위
 * Spring은 더 구체적으로 매칭되는 예외 핸들러를 우선 선택한다.
 * BusinessException → handleBusinessException
 * MethodArgumentNotValidException → handleMethodArgumentNotValid
 * 그 외 예외 → handleException
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        String errorMessage = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(errorMessage));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.onFailure(ex.getErrorCode()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("입력값이 유효하지 않습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure("서버 내부 오류: " + ex.getMessage()));
    }
}
