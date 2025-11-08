package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select distinct p
        from Post p
        left join fetch p.prompts
        where p.id = :postId
    """)
    Optional<Post> findWithAllById(@Param("postId") Long postId);
}