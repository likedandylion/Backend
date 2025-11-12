package com.likedandylion.prome.comment.service;

import com.likedandylion.prome.comment.dto.CommentCreateRequest;
import com.likedandylion.prome.comment.dto.CommentUpdateRequest;
import com.likedandylion.prome.comment.dto.CommentLikeResponse;
import com.likedandylion.prome.comment.dto.CommentResponse;
import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.comment.repository.CommentRepository;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     */
    public List<CommentResponse> getComments(Long postId) {
        // 1. 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 2. N+1 문제 해결을 위해 Fetch Join 쿼리 사용
        List<Comment> comments = commentRepository.findAllByPostIdWithUserAndReactions(postId);

        // 3. DTO로 변환하여 반환
        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글에 새로운 댓글을 작성합니다.
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long userId) {
        // 1. 사용자(User)와 게시글(Post) 엔티티를 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. Comment 엔티티 생성 및 저장
        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 3. DTO로 변환하여 반환
        return CommentResponse.from(savedComment);
    }

    /**
     * 기존 댓글을 수정합니다.
     */
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 2. 권한 확인 (댓글 작성자와 로그인한 사용자가 동일한지)
        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("댓글을 수정할 권한이 없습니다.");
        }

        // 3. 내용 수정 (JPA 변경 감지)
        comment.updateContent(request.getContent());

        // 4. DTO로 변환하여 반환
        return CommentResponse.from(comment);
    }

    /**
     * 댓글을 삭제합니다.
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 2. 권한 확인 (댓글 작성자와 로그인한 사용자가 동일한지)
        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }

        // 3. 삭제 (Reaction, Bookmark 등과 달리 Comment는 하위 요소를 가질 수 있으나,
        // 현재 설계상 CommentLike가 Reaction으로 통합되었으므로 ReactionRepository에서 관련 데이터를 먼저 삭제)

        // 3-1. 이 댓글에 달린 모든 반응(Reaction) 기록 삭제
        reactionRepository.deleteByComment(comment);

        // 3-2. 댓글 삭제
        commentRepository.delete(comment);
    }

    /**
     * 댓글에 '좋아요'를 누르거나 취소합니다 (토글 방식).
     * Reaction 엔티티를 사용합니다.
     */
    @Transactional
    public CommentLikeResponse toggleLikeComment(Long commentId, Long userId) {
        // 1. 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 2. 기존에 'LIKE' 반응을 했는지 조회
        Optional<Like> existingLike = reactionRepository.findByUserAndCommentAndType(user, comment, ReactionType.LIKE);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 3a. 이미 좋아요를 눌렀다면 -> 삭제 (좋아요 취소)
            reactionRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            // 3b. 좋아요를 누르지 않았다면 -> 생성 (좋아요)
            // (만약 DISLIKE를 누른 상태였다면, DISLIKE는 삭제해주는 로직이 추가되면 더 좋습니다)
            Like newLike = Like.builder()
                    .user(user)
                    .comment(comment)
                    .type(ReactionType.LIKE)
                    .build();
            reactionRepository.save(newLike);
            isLiked = true;
        }

        // 4. 최종 좋아요 개수 집계 (비효율적일 수 있으나, Comment 엔티티에 count 필드가 없으므로 직접 카운트)
        int finalLikesCount = reactionRepository.countByCommentAndType(comment, ReactionType.LIKE);

        // 5. DTO로 변환하여 반환
        return new CommentLikeResponse(finalLikesCount, isLiked);
    }
}