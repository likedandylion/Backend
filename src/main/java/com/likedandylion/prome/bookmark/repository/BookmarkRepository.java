package com.likedandylion.prome.bookmark.repository;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 사용자와 게시글 ID로 특정 북마크가 존재하는지 확인합니다. (토글 기능에 사용)
     */
    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * 특정 사용자의 모든 북마크 목록을 조회합니다. (N+1 문제 발생 가능성 있음)
     * findByUserIdWithPostAndUser 메서드 사용을 권장합니다.
     */
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * N+1 문제를 해결하기 위해 Fetch Join을 사용하는 쿼리입니다.
     * 북마크 목록 조회 시, 연관된 게시글(Post)과 게시글의 작성자(User) 정보까지
     * 한 번의 쿼리로 모두 가져옵니다.
     */
    @Query("SELECT b FROM Bookmark b " +
            "JOIN FETCH b.post p " +
            "JOIN FETCH p.user u " +
            "WHERE b.user.id = :userId " +
            "ORDER BY b.createdAt DESC")
    List<Bookmark> findByUserIdWithPostAndUser(@Param("userId") Long userId);
}