package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.DeleteMyCommentsRequest;
import com.likedandylion.prome.user.dto.DeleteMyCommentsResponse;

public interface UserCommentCommandService {

    /**
     * 내가 쓴 댓글 중 선택한 댓글들을 삭제합니다.
     * - 존재하지 않거나, 내 소유가 아닌 댓글은 삭제되지 않습니다.
     */
    DeleteMyCommentsResponse deleteMyComments(Long userId, DeleteMyCommentsRequest request);
}