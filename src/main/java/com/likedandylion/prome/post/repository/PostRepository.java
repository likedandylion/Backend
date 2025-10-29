package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"prompts", "user", "reactions"})
    Optional<Post> findWithAllById(Long id);
}