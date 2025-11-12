package com.likedandylion.prome.user.dto;

import java.util.List;

/**
 * 선택 삭제 결과를 요약해서 돌려주는 응답 DTO(record).
 * - deletedIds: 실제로 삭제된 게시글 ID 목록
 * - skippedIds: 요청했지만 삭제되지 않은 ID들(존재X/내 소유 아님 등)
 * - deletedCount: 삭제된 개수
 */
public record DeleteMyPostsResponse(
        List<Long> deletedIds,
        List<Long> skippedIds,
        int deletedCount
) {
    public static DeleteMyPostsResponse of(List<Long> deletedIds, List<Long> skippedIds) {
        return new DeleteMyPostsResponse(
                deletedIds,
                skippedIds,
                deletedIds == null ? 0 : deletedIds.size()
        );
    }
}