package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * "내가 쓴 댓글" 조회/삭제 전용 리포지토리
 * - Comment.user (ManyToOne User) 연관 필드를 통해
 * - 현재 사용자(userId)의 댓글을 createdAt 내림차순으로 조회/삭제합니다.
 */
public interface UserCommentRepository extends JpaRepository<Comment, Long> {

    // (이미 있음) 내 댓글 최신순 조회
    List<Comment> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    // [선택 삭제 준비 1] 내가 쓴 댓글 중, 요청한 ID 목록에 속하는 댓글만 선별 조회
    //  - Service에서 deletedIds / skippedIds 계산에 사용
    List<Comment> findAllByUser_IdAndIdIn(Long userId, Collection<Long> commentIds);

    // [선택 삭제 준비 2] 내가 쓴 댓글 중, 요청한 ID 목록에 속하는 댓글을 일괄 삭제
    //  - 반환값: 실제 삭제된 row 개수 (= deletedCount와 일치)
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    long deleteByUser_IdAndIdIn(Long userId, Collection<Long> commentIds);
}