package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Post.user(연관필드) 기준으로
 * - 내 글 목록 조회(기존)
 * - 내 글 중 선택 삭제(신규)
 * 를 지원합니다.
 */
public interface UserPostRepository extends JpaRepository<Post, Long> {

    // 내 글 최신순 조회
    List<Post> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    // [선택 삭제 준비 1] 삭제 대상 "내 글"만 선별 조회 (존재/소유권 필터)
    // - Service에서 deletedIds/ skippedIds 계산에 사용
    List<Post> findAllByUser_IdAndIdIn(Long userId, Collection<Long> postIds);

    // [선택 삭제 준비 2] 실제 일괄 삭제 쿼리
    // - 반환값: 삭제된 row 개수
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    long deleteByUser_IdAndIdIn(Long userId, Collection<Long> postIds);
}
