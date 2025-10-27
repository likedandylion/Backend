package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.DeleteMyPostsRequest;
import com.likedandylion.prome.user.dto.DeleteMyPostsResponse;

public interface UserPostCommandService {

    /**
     * 내가 쓴 글 중 선택된 게시글들을 삭제합니다.
     * @param userId 현재 로그인한 사용자 ID
     * @param req 삭제할 postId 목록 요청
     * @return 삭제 결과 요약 (삭제된 ID / 건너뛴 ID / 개수)
     */
    DeleteMyPostsResponse deleteMyPosts(Long userId, DeleteMyPostsRequest req);
}
