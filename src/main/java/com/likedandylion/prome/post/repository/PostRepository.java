package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

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

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user u",
            countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    @Query("""
    select p from Post p
    left join fetch p.prompts
    left join fetch p.likes
    join fetch p.user
    where p.id = :postId""")
    Optional<Post> findByIdWithDetail(Long postId);
}