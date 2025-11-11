package com.likedandylion.prome.reaction.repository;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    Optional<Like> findByUserAndComment(User user, Comment comment);

    int countByPost(Post post);
    int countByComment(Comment comment);
    boolean existsByUserAndPost(User user, Post post);
    boolean existsByUserAndComment(User user, Comment comment);
}

