package com.likedandylion.prome.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 내가 쓴 댓글 중 선택한 댓글들을 삭제하기 위한 요청 바디.
 * - method: DELETE
 * - endpoint: /api/v1/users/me/comments
 * - 예시 바디: { "commentIds": [201, 202, 999] }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMyCommentsRequest {

    @NotEmpty(message = "삭제할 댓글 ID 목록(commentIds)은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "한 번에 최대 100개까지 삭제할 수 있습니다.")
    private List<Long> commentIds; // 삭제 대상 댓글 ID 목록
}