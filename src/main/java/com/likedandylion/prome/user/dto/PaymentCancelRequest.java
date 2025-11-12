package com.likedandylion.prome.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구독/결제 취소 요청 바디
 * - 실무에선 paymentKey(또는 주문번호)가 필수인 경우가 많습니다.
 * - reason(취소 사유)은 나중에 CS/로그에 유용합니다.
 *
 * 예시:
 * {
 *   "paymentKey": "pay_1234567890",
 *   "reason": "사용하지 않음"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {

    // 결제대행사(PG) 고유 키(또는 내부 주문번호/영수증번호)
    // 게이트웨이 미연동이라면 null 허용 가능하지만, 형식은 남겨둡니다.
    private String paymentKey;

    // 최소한 취소 사유는 받아두면 좋아요 (CS/감사로그용)
    @NotBlank(message = "취소 사유(reason)는 비어 있을 수 없습니다.")
    private String reason;
}