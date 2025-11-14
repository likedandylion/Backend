package com.likedandylion.prome.advertisement.dto;

import com.likedandylion.prome.advertisement.entity.Advertisement;
import lombok.Getter;

@Getter
public class AdListResponse {
    private Long adId;
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
    private int blueTicketReward;
    private int greenTicketReward;

    private AdListResponse(Advertisement ad) {
        this.adId = ad.getId();
        this.title = ad.getTitle();
        this.thumbnailUrl = ad.getThumbnailUrl();
        this.videoUrl = ad.getVideoUrl();
        this.blueTicketReward = ad.getBlueTicketReward();
        this.greenTicketReward = ad.getGreenTicketReward();
    }

    public static AdListResponse from(Advertisement ad) {
        return new AdListResponse(ad);
    }
}