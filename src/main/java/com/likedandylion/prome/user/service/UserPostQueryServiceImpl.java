// 파일: src/main/java/com/likedandylion/prome/user/service/UserPostQueryServiceImpl.java
package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.PostSimpleResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.user.repository.UserPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPostQueryServiceImpl implements UserPostQueryService {

    private final UserPostRepository userPostRepository;

    @Override
    public List<PostSimpleResponse> getMyPosts(Long userId) {
        List<Post> posts = userPostRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(PostSimpleResponse::from)
                .toList();
    }
}
