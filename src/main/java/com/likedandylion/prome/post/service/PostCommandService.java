package com.likedandylion.prome.post.service;

import com.likedandylion.prome.global.exception.BadRequestException;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.post.dto.PostCreateRequest;
import com.likedandylion.prome.post.dto.PostCreateResponse;
import com.likedandylion.prome.post.dto.PostUpdateRequest;
import com.likedandylion.prome.post.dto.PostUpdateResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.prompt.entity.PromptType;
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
    public PostCreateResponse create(Long userId, PostCreateRequest req) {
        // 1. 작성자 조회 (JWT로 인증된 유저)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));

        // 2. 게시글 생성
        Post post = new Post(user, req.getTitle(), Status.ACTIVE);
        postRepository.save(post);

        // 3. 프롬프트 생성
        req.getPrompts().forEach((key, content) -> {
            PromptType type = switch (key.toLowerCase()) {
                case "chatgpt" -> PromptType.GPT;
                case "gemini" -> PromptType.GEMINI;
                case "claude" -> PromptType.CLAUDE;
                default -> throw new BadRequestException("INVALID_PROMPT_TYPE", "잘못된 모델 타입입니다.");
            };
            post.addPrompt(new Prompt(post, type, content));
        });

        return new PostCreateResponse(post.getId(), "success", "게시글이 성공적으로 작성되었습니다.");
    }

    @Transactional
    public PostUpdateResponse update(Long postId, PostUpdateRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_POST", "게시글을 찾을 수 없습니다."));

        // 1) 제목
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            post.updateTitle(req.getTitle());
        }

        // 2) 상태
        if (req.getStatus() != null) {
            post.updateStatus(req.getStatus());
        }

        // 3) 프롬프트 (GPT / GEMINI / CLAUDE 키 기준)
        if (req.getPrompts() != null && !req.getPrompts().isEmpty()) {
            req.getPrompts().forEach((k, v) -> {
                if (v == null || v.isBlank()) return; // 내용 없으면 skip

                PromptType type = switch (k.toLowerCase()) {
                    case "chatgpt" -> PromptType.GPT;
                    case "gemini" -> PromptType.GEMINI;
                    case "claude" -> PromptType.CLAUDE;
                    default -> throw new BadRequestException("UNSUPPORTED_PROMPT_TYPE", "지원하지 않는 프롬프트 타입입니다.");
                };

                // 기존 프롬프트 있으면 내용만 교체, 없으면 생성
                Prompt prompt = post.getPrompts()
                        .stream()
                        .filter(p -> p.getType() == type)
                        .findFirst()
                        .orElseGet(() -> {
                            Prompt np = new Prompt(post, type, v);
                            post.addPrompt(np);
                            return np;
                        });

                prompt.changeContent(v);
            });
        }

        post.touchUpdatedAt();
        return PostUpdateResponse.of(post.getId(), "게시글이 수정되었습니다.");
    }
}