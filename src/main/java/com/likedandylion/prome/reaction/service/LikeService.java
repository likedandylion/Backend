package com.likedandylion.prome.reaction.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.comment.repository.CommentRepository;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.reaction.repository.LikeRepository;
import com.likedandylion.prome.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void likePost(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        if (likeRepository.existsByUserAndPost(user, post)) {
            return; // 이미 좋아요 되어있으면 아무 작업 안함
        }

        Like like = new Like(user, post, null);
        likeRepository.save(like);
    }

    // 게시글 좋아요 취소
    public void unlikePost(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        likeRepository.findByUserAndPost(user, post)
                .ifPresent(likeRepository::delete);
    }

    // 댓글 좋아요
    public void likeComment(Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        if (likeRepository.existsByUserAndComment(user, comment)) {
            return;
        }

        Like like = new Like(user, null, comment);
        likeRepository.save(like);
    }

    // 댓글 좋아요 취소
    public void unlikeComment(Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(likeRepository::delete);
    }



    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
    }
}