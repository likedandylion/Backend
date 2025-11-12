package com.likedandylion.prome.user.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.user.dto.CommentSimpleResponse;
import com.likedandylion.prome.user.repository.UserCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserCommentRepository를 사용해
 * - 내 댓글 최신순 조회
 * - 엔티티 → DTO(CommentSimpleResponse) 변환
 * 을 담당하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용 트랜잭션 (성능/안전)
public class UserCommentQueryServiceImpl implements UserCommentQueryService {

    private final UserCommentRepository userCommentRepository;

    @Override
    public List<CommentSimpleResponse> getMyComments(Long userId) {
        // 1) 리포지토리에서 내 댓글 최신순으로 가져오기
        List<Comment> comments = userCommentRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);

        // 2) 엔티티 → DTO 변환 (규칙: DTO 내부 정적 메서드 from(...))
        return comments.stream()
                .map(CommentSimpleResponse::from)
                .toList();
    }
}