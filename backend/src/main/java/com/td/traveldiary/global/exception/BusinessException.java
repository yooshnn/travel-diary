package com.td.traveldiary.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * # RuntimeException 상속 이유
 * Checked Exception(Exception 상속)이면 호출하는 모든 곳에서 try-catch나 throws 선언이 강제된다.
 * RuntimeException을 상속하면 컴파일러가 처리를 강제하지 않아 그냥 던지기만 하면 되고,
 * GlobalExceptionHandler가 한 곳에서 모아서 처리한다.
 */
@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}
