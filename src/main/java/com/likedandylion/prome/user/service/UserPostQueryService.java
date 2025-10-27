package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.PostSimpleResponse;

import java.util.List;

/**
 * "내가 쓴 글" 조회 전용 쿼리 서비스 (user 도메인 관점)
 * - 컨트롤러는 이 결과만 받아 응답으로 포맷팅합니다.
 */
public interface UserPostQueryService {
    /**
     * 현재 로그인 사용자(userId)의 글을 최신순으로 조회합니다.
     * @param userId 현재 로그인한 사용자 ID
     * @return PostSimpleResponse 리스트(최신순)
     */
    List<PostSimpleResponse> getMyPosts(Long userId);
}