package kr.hs.dgsw.javasuhaeng.global.common;

import kr.hs.dgsw.javasuhaeng.global.exception.ErrorResponse;

public record ApiResponse<T>(boolean success, T data, ErrorResponse error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> fail(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
}
