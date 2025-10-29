package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostCreateRequest;
import com.likedandylion.prome.post.dto.PostCreateResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.prompt.entity.Type;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        // 1. 작성자 조회
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 게시글 생성
        Post post = new Post(user, req.getTitle(), Status.FREE);
        postRepository.save(post);

        // 3. 프롬프트 추가 (map 기반)
        req.getPrompts().forEach((key, content) -> {
            Type type = switch (key.toLowerCase()) {
                case "chatgpt" -> Type.GPT;
                case "gemini" -> Type.GEMINI;
                case "claude" -> Type.CLAUDE;
                default -> throw new IllegalArgumentException("잘못된 모델 타입: " + key);
            };
            post.getPrompts().add(new Prompt(post, type, content));
        });

        return new PostCreateResponse(post.getId(), "success", "게시글이 성공적으로 작성되었습니다.");
    }
}