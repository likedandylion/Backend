package com.likedandylion.prome.comment.service;

import com.likedandylion.prome.comment.dto.CommentCreateRequest;
import com.likedandylion.prome.comment.dto.CommentUpdateRequest;
import com.likedandylion.prome.comment.dto.CommentLikeResponse;
import com.likedandylion.prome.comment.dto.CommentResponse;
import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.comment.repository.CommentRepository;
import com.likedandylion.prome.global.exception.ForbiddenException;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.reaction.repository.LikeRepository;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
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
    private final LikeRepository reactionRepository;

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     */
    public List<CommentResponse> getComments(Long postId) {
        // 1. 게시글 존재 여부 확인
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("NOT_FOUND_POST", "존재하지 않는 게시글입니다.");
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
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_POST", "게시글을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_COMMENT", "댓글을 찾을 수 없습니다."));

        // 2. 권한 확인 (댓글 작성자와 로그인한 사용자가 동일한지)
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN_COMMENT_ACCESS", "댓글을 수정할 권한이 없습니다.");
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
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_COMMENT", "댓글을 찾을 수 없습니다."));

        // 2. 권한 확인 (댓글 작성자와 로그인한 사용자가 동일한지)
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN_COMMENT_ACCESS", "댓글을 삭제할 권한이 없습니다.");
        }

        // 3. 삭제 (Reaction, Bookmark 등과 달리 Comment는 하위 요소를 가질 수 있으나,
        // 현재 설계상 CommentLike가 Reaction으로 통합되었으므로 ReactionRepository에서 관련 데이터를 먼저 삭제)

        // 3-1. 이 댓글에 달린 모든 반응(Reaction) 기록 삭제
        reactionRepository.deleteByComment(comment);

        // 3-2. 댓글 삭제
        commentRepository.delete(comment);
    }
}