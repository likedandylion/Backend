package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.CommentSimpleResponse;

import java.util.List;

/**
 * "내가 쓴 댓글" 조회 전용 쿼리 서비스.
 * 컨트롤러는 이 결과만 받아 응답으로 감싸서 반환합니다.
 */
public interface UserCommentQueryService {

    /**
     * 현재 로그인 사용자(userId)의 댓글을 최신순으로 조회합니다.
     * @param userId 로그인한 사용자 ID
     * @return 최신순 CommentSimpleResponse 리스트
     */
    List<CommentSimpleResponse> getMyComments(Long userId);
}