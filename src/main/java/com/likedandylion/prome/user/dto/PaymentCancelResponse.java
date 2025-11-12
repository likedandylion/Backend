package com.likedandylion.prome.user.dto;

import java.time.LocalDateTime;

/**
 * 구독/결제 취소 결과 응답 DTO(record)
 * - canceled: 취소 처리 여부
 * - paymentKey: 어떤 결제건을 취소했는지(있다면)
 * - canceledAt: 서버 기준 취소 처리 시각
 * - note: 추가 메시지(사유/설명)
 *
 * 프론트는 이걸 ApiResponse로 감싼 형태로 받게 됩니다.
 */
public record PaymentCancelResponse(
        boolean canceled,
        String paymentKey,
        LocalDateTime canceledAt,
        String note
) {
    /**
     * 편의 팩토리: 성공 케이스 빠르게 생성
     */
    public static PaymentCancelResponse success(String paymentKey, String note) {
        return new PaymentCancelResponse(true, paymentKey, LocalDateTime.now(), note);
        // createdAt/updatedAt처럼 @PrePersist를 쓰지 않고, 응답 시각을 바로 넣어줍니다.
    }

    /**
     * 편의 팩토리: 실패/스킵 케이스 생성
     */
    public static PaymentCancelResponse fail(String paymentKey, String note) {
        return new PaymentCancelResponse(false, paymentKey, LocalDateTime.now(), note);
    }
}