package com.td.traveldiary.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
 * # enum
 * 미리 정해진 상수 집합을 표현하는 타입.
 * 일반 클래스처럼 필드와 메서드를 가질 수 있다.
 * 각 상수(INVALID_INPUT_VALUE 등)는 ErrorCode의 인스턴스이며, (400, "...") 부분이 생성자 호출이다.
 *
 * # enum 생성자
 * enum 생성자는 항상 private이다.
 * @AllArgsConstructor가 모든 필드를 받는 private 생성자를 만들어준다.
 *
 * # name(), ordinal()
 * 모든 enum이 기본으로 제공하는 메서드다.
 * name()은 상수 이름("INVALID_INPUT_VALUE"), ordinal()은 선언 순서(0부터)를 반환한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    SIDO_NOT_FOUND(404, "존재하지 않는 시도입니다."),

    UNAUTHORIZED(401, "인증 정보가 없습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    UNSUPPORTED_PROVIDER(500, "지원하지 않는 OAuth2 제공자입니다.");

    private final int httpStatus;
    private final String message;
}
