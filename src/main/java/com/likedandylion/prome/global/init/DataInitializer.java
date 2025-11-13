package com.likedandylion.prome.global.init;

import com.likedandylion.prome.advertisement.entity.Advertisement;
import com.likedandylion.prome.advertisement.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdvertisementRepository adRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (adRepository.count() == 0) {
            log.info("Advertisement 테이블이 비어있어, 6개의 샘플 데이터를 삽입합니다.");

            Advertisement ad1 = Advertisement.builder()
                    .title("붕괴: 스타레일")
                    .thumbnailUrl("https://img.youtube.com/vi/hgTscLO7odo/0.jpg")
                    .videoUrl("https://youtu.be/hgTscLO7odo?si=-mMP9sv89lszZMe2")
                    .blueTicketReward(2)
                    .greenTicketReward(0)
                    .build();

            Advertisement ad2 = Advertisement.builder()
                    .title("젠레스 존 제로")
                    .thumbnailUrl("https://img.youtube.com/vi/isYwc9YtwR4/0.jpg")
                    .videoUrl("https://youtu.be/isYwc9YtwR4?si=vN5q8qBnYWWKYtWv")
                    .blueTicketReward(0)
                    .greenTicketReward(1)
                    .build();

            Advertisement ad3 = Advertisement.builder()
                    .title("명조: 워더링 웨이브")
                    .thumbnailUrl("https://img.youtube.com/vi/6_bbVW9u-xw/0.jpg")
                    .videoUrl("https://youtu.be/6_bbVW9u-xw?si=NDWTykbc9danNlpN")
                    .blueTicketReward(0)
                    .greenTicketReward(1)
                    .build();

            Advertisement ad4 = Advertisement.builder()
                    .title("한화손해보험")
                    .thumbnailUrl("https://img.youtube.com/vi/tF0rJJrd7FU/0.jpg")
                    .videoUrl("https://youtu.be/tF0rJJrd7FU?si=pItsqPi7VZdcW5G_")
                    .blueTicketReward(2)
                    .greenTicketReward(0)
                    .build();

            Advertisement ad5 = Advertisement.builder()
                    .title("한국외국어대학교")
                    .thumbnailUrl("https://img.youtube.com/vi/JsJ2tP9F1jE/0.jpg")
                    .videoUrl("https://youtu.be/JsJ2tP9F1jE?si=JF04uzS__8aTiqVd")
                    .blueTicketReward(0)
                    .greenTicketReward(1)
                    .build();

            Advertisement ad6 = Advertisement.builder()
                    .title("멋쟁이사자처럼")
                    .thumbnailUrl("https://img.youtube.com/vi/DdBgBXmSI38/0.jpg")
                    .videoUrl("https://youtu.be/DdBgBXmSI38?si=NM6wf6IgXYBTYsyd")
                    .blueTicketReward(2)
                    .greenTicketReward(0)
                    .build();

            adRepository.saveAll(List.of(ad1, ad2, ad3, ad4, ad5, ad6));
            log.info("샘플 광고 6개 삽입 완료.");

        } else {
            log.info("Advertisement 테이블에 이미 데이터가 존재합니다. 초기화를 건너뜁니다.");
        }
    }
}