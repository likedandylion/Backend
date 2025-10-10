package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(Status status, Pageable pageable);
}