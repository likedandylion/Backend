package com.likedandylion.prome.advertisement.controller;

import com.likedandylion.prome.advertisement.dto.AdListResponse;
import com.likedandylion.prome.advertisement.dto.AdWatchResponse;
import com.likedandylion.prome.advertisement.service.AdvertisementService;
import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ads")
public class AdvertisementController {

    private final AdvertisementService adService;

    @Operation(summary = "광고 목록 조회", description = "시청 가능한 광고 목록과 보상을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdListResponse>>> getAdList() {
        List<AdListResponse> data = adService.getAdList();
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "광고 목록 조회 성공", data));
    }

    @Operation(summary = "광고 시청 완료 (티켓 충전)", description = "광고 시청 완료 후 티켓을 충전합니다. (하루 2회 제한)")
    @PostMapping("/{adId}/watch")
    public ResponseEntity<ApiResponse<AdWatchResponse>> watchAd(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long adId
    ) {
        Long userId = principal.getId();
        AdWatchResponse data = adService.watchAd(userId, adId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "티켓이 충전되었습니다.", data));
    }
}