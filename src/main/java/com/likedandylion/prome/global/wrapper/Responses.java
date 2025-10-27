package com.likedandylion.prome.global.wrapper;

public final class Responses {

    // 유틸 클래스이므로 인스턴스화 방지
    private Responses() {}

    /** 성공 응답: code는 관례대로 "OK" */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, "OK", message, data);
    }

    /** 실패 응답: code와 message를 명시 */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
