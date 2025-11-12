package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
           SELECT DISTINCT p
             FROM Post p
             LEFT JOIN p.prompts pr
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR pr.content      LIKE CONCAT('%', :kw, '%')
           """)
    Page<Post> searchByKeyword(@Param("kw") String keyword, Pageable pageable);
}