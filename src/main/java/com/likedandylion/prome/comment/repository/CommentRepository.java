package com.likedandylion.prome.comment.repository;

import com.likedandylion.prome.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
