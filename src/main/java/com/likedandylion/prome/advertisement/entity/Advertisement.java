package com.likedandylion.prome.advertisement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private int blueTicketReward;

    @Column(nullable = false)
    private int greenTicketReward;

    @Builder
    public Advertisement(String title, String thumbnailUrl, String videoUrl, int blueTicketReward, int greenTicketReward) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.blueTicketReward = blueTicketReward;
        this.greenTicketReward = greenTicketReward;
    }
}