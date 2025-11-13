package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 🔍 프롬프트 + 제목 검색 (정상 동작)
    @Query(value = """
           SELECT DISTINCT p
             FROM Post p
             JOIN FETCH p.user u
             LEFT JOIN p.prompts pr
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR pr.content      LIKE CONCAT('%', :kw, '%')
           """,
            countQuery = """
           SELECT COUNT(DISTINCT p)
             FROM Post p
             LEFT JOIN p.prompts pr
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR pr.content      LIKE CONCAT('%', :kw, '%')
           """)
    Page<Post> searchByKeyword(@Param("kw") String keyword, Pageable pageable);

    // 🔍 전체 목록 조회 (User만 fetch)
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user u",
            countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    // 🔍 게시글 상세조회 (⚠️ prompts만 fetch join — likes 제거됨!)
    @Query("""
        SELECT DISTINCT p
          FROM Post p
          JOIN FETCH p.user u
          LEFT JOIN FETCH p.prompts pr
         WHERE p.id = :postId
        """)
    Optional<Post> findByIdWithDetail(@Param("postId") Long postId);
}