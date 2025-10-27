package com.likedandylion.prome.user.dto;

import com.likedandylion.prome.user.entity.User;

public record UserMeResponse(
        String nickname,
        String profileImageUrl,
        int blueTickets,
        int greenTickets,
        boolean isPremium
) {
    public static UserMeResponse from(User user) {
        // 엔티티에 아직 해당 필드가 없으므로 임시로 빈 문자열 사용
        String profileImageUrl = "";

        // 엔티티 필드명은 blueTicket/greenTicket (단수) -> 게터도 getBlueTicket()/getGreenTicket()
        int blueTickets = user.getBlueTicket();
        int greenTickets = user.getGreenTicket();

        // 프리미엄 여부는 일단 구독(Subscription) 존재 여부로 판단(추후 isActive 같은 필드가 생기면 교체)
        boolean isPremium = (user.getSubscription() != null);

        return new UserMeResponse(
                user.getNickname(),
                profileImageUrl,
                blueTickets,
                greenTickets,
                isPremium
        );
    }
}
