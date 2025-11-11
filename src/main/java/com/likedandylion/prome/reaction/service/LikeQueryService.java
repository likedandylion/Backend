package com.likedandylion.prome.reaction.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.comment.repository.CommentRepository;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.reaction.repository.LikeRepository;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeQueryService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public int getPostLikeCount(Long postId){
        Post post = getPost(postId);
        return likeRepository.countByPost(post);
    }

    @Transactional(readOnly = true)
    public int getCommentLikeCount(Long commentId) {
        Comment comment = getComment(commentId);
        return likeRepository.countByComment(comment);
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
