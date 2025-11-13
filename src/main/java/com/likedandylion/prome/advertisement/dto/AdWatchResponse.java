package com.likedandylion.prome.advertisement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdWatchResponse {
    private int totalBlueTickets; // 충전 후 총 블루 티켓
    private int totalGreenTickets; // 충전 후 총 그린 티켓
    private int adsWatchedToday; // 오늘 시청 횟수
}