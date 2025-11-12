package com.likedandylion.prome.user.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.user.dto.CommentSimpleResponse;
import com.likedandylion.prome.user.dto.PostSimpleResponse;
import com.likedandylion.prome.user.repository.UserCommentRepository;
import com.likedandylion.prome.user.repository.UserPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserContentQueryService {
    private final UserCommentRepository userCommentRepository;
    private final UserPostRepository userPostRepository;

    /** 내가 쓴 댓글 최신순 조회 */
    public List<CommentSimpleResponse> getMyComments(Long userId) {
        List<Comment> comments = userCommentRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
        return comments.stream()
                .map(CommentSimpleResponse::from)
                .toList();
    }

    /** 내가 쓴 글 최신순 조회 */
    public List<PostSimpleResponse> getMyPosts(Long userId) {
        List<Post> posts = userPostRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(PostSimpleResponse::from)
                .toList();
    }
}
