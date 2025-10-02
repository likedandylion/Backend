package com.likedandylion.prome.post.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
