package com.td.traveldiary.global.response;

import com.td.traveldiary.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * # 정적 팩토리 메서드 패턴
 * 생성자를 private으로 막고 onSuccess, onFailure로만 인스턴스를 만들게 강제한다.
 * new ApiResponse<>(false, data, null) 같은 잘못된 조합을 사전에 방지한다.
 *
 * # class 사용 이유
 * 생성자를 숨기려면 @AllArgsConstructor(access = AccessLevel.PRIVATE)가 필요하다.
 * record는 생성자를 private으로 만들 수 없으므로 class를 사용했다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> onFailure(String message) {
        return new ApiResponse<>(false, null, message);
    }
    public static <T> ApiResponse<T> onFailure(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getMessage());
    }
}
