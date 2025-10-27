package com.likedandylion.prome.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 내가 쓴 글 중 선택한 글들을 삭제하기 위한 요청 바디.
 * - method: DELETE
 * - endpoint: /api/v1/users/me/posts
 * - body: { "postIds": [1, 2, 3] }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMyPostsRequest {

    @NotEmpty(message = "삭제할 게시글 ID 목록(postIds)은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "한 번에 최대 100개까지 삭제할 수 있습니다.")
    private List<Long> postIds;
}
