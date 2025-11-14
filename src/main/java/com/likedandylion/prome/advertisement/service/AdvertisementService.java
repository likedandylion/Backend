package com.likedandylion.prome.advertisement.service;

import com.likedandylion.prome.advertisement.dto.AdListResponse;
import com.likedandylion.prome.advertisement.dto.AdWatchResponse;
import com.likedandylion.prome.advertisement.entity.Advertisement;
import com.likedandylion.prome.advertisement.repository.AdvertisementRepository;
import com.likedandylion.prome.global.exception.BadRequestException;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.user.entity.AdWatchLog;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.AdWatchLogRepository;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;
    private final AdWatchLogRepository adLogRepository;

    private static final int DAILY_AD_LIMIT = 999;

    @Transactional(readOnly = true)
    public List<AdListResponse> getAdList() {
        return adRepository.findAll().stream()
                .map(AdListResponse::from)
                .toList();
    }

    @Transactional
    public AdWatchResponse watchAd(Long userId, Long adId) {
        // 일일 2회 시청 제한 검사
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        int watchedCount = adLogRepository.countByUserIdAndWatchedAtAfter(userId, todayStart);

        if (watchedCount >= DAILY_AD_LIMIT) {
            throw new BadRequestException("DAILY_AD_LIMIT", "하루 광고 시청 횟수를 초과했습니다. (일 2회)");
        }

        // 사용자 및 광고 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));

        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_AD", "광고를 찾을 수 없습니다."));

        // 티켓 충전
        user.addTickets(ad.getBlueTicketReward(), ad.getGreenTicketReward());

        // 시청 기록 저장
        AdWatchLog log = new AdWatchLog(user);
        adLogRepository.save(log);

        return new AdWatchResponse(
                user.getBlueTicket(),
                user.getGreenTicket(),
                watchedCount + 1
        );
    }
}