package com.likedandylion.prome.user.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.user.dto.DeleteMyCommentsRequest;
import com.likedandylion.prome.user.dto.DeleteMyCommentsResponse;
import com.likedandylion.prome.user.dto.DeleteMyPostsRequest;
import com.likedandylion.prome.user.dto.DeleteMyPostsResponse;
import com.likedandylion.prome.user.repository.UserCommentRepository;
import com.likedandylion.prome.user.repository.UserPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserContentCommandService {
    private final UserCommentRepository userCommentRepository;
    private final UserPostRepository userPostRepository;

    /**
     * 내가 쓴 댓글 중 선택한 댓글들을 삭제합니다.
     * - 존재하지 않거나, 내 소유가 아닌 댓글은 삭제되지 않습니다.
     */
    public DeleteMyCommentsResponse deleteMyComments(Long userId, DeleteMyCommentsRequest request) {
        List<Long> requestedIds = request.getCommentIds();
        if (requestedIds == null || requestedIds.isEmpty()) {
            throw new NotFoundException("COMMENT_ID_REQUIRED", "삭제할 댓글 ID가 필요합니다.");
        }

        // 1. 요청 ID 중 내 댓글만 필터링
        List<Comment> myComments = userCommentRepository.findAllByUser_IdAndIdIn(userId, requestedIds);
        Set<Long> myCommentIds = myComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());

        // 2. 실제 삭제 (DB 반영)
        long deletedCount = userCommentRepository.deleteByUser_IdAndIdIn(userId, myCommentIds);

        // 3. 삭제되지 않은 ID(= 존재X or 남의 댓글)
        List<Long> skippedIds = new ArrayList<>(requestedIds);
        skippedIds.removeAll(myCommentIds);

        return new DeleteMyCommentsResponse(
                new ArrayList<>(myCommentIds),
                skippedIds,
                (int) deletedCount
        );
    }

    /**
     * 내가 쓴 게시글 중 선택된 글들을 삭제합니다.
     * - 존재하지 않거나, 내 소유가 아닌 글은 삭제되지 않습니다.
     */
    public DeleteMyPostsResponse deleteMyPosts(Long userId, DeleteMyPostsRequest req) {
        List<Long> requestIds = req.getPostIds();
        if (requestIds == null || requestIds.isEmpty()) {
            throw new NotFoundException("POST_ID_REQUIRED", "삭제할 게시글 ID가 필요합니다.");
        }

        // 1. 실제 DB에서 "내가 쓴 글"만 조회
        List<Post> myPosts = userPostRepository.findAllByUser_IdAndIdIn(userId, requestIds);
        List<Long> myPostIds = myPosts.stream()
                .map(Post::getId)
                .toList();

        // 2. "삭제 가능한 글"과 "스킵될 글" 구분
        Set<Long> foundSet = Set.copyOf(myPostIds);
        List<Long> skippedIds = requestIds.stream()
                .filter(id -> !foundSet.contains(id))
                .collect(Collectors.toList());

        // 3. 실제 삭제 실행
        long deletedCount = userPostRepository.deleteByUser_IdAndIdIn(userId, myPostIds);

        return new DeleteMyPostsResponse(myPostIds, skippedIds, (int) deletedCount);
    }
}
