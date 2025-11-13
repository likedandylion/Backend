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

    /**
     * 프롬프트/게시글 생성
     */
    @Transactional
    public PostCreateResponse create(Long userId, PostCreateRequest req) {
        // 1) 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));

        // 2) 게시글 엔티티 생성
        Post post = Post.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .category(req.getCategory())
                .tags(req.getTags())
                .status(Status.ACTIVE)
                .build();

        // 3) 먼저 게시글 저장 (PK 필요)
        postRepository.save(post);

        // 4) 프롬프트 3종(GPT / GEMINI / CLAUDE) 생성
        if (req.getPrompts() != null && !req.getPrompts().isEmpty()) {
            req.getPrompts().forEach((key, value) -> {
                if (value == null || value.isBlank()) return;

                PromptType type = switch (key.toLowerCase()) {
                    case "chatgpt" -> PromptType.GPT;
                    case "gemini" -> PromptType.GEMINI;
                    case "claude" -> PromptType.CLAUDE;
                    default -> throw new BadRequestException("INVALID_PROMPT_TYPE", "잘못된 모델 타입입니다.");
                };

                Prompt prompt = new Prompt(post, type, value);
                // 연관관계 편의 메서드 사용 (Post 쪽 리스트에도 추가)
                post.addPrompt(prompt);
            });
        }

        return new PostCreateResponse(post.getId(), "success", "게시글이 성공적으로 작성되었습니다.");
    }

    /**
     * 프롬프트/게시글 수정
     */
    @Transactional
    public PostUpdateResponse update(Long postId, PostUpdateRequest req) {
        // 1) 기존 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_POST", "게시글을 찾을 수 없습니다."));

        // 2) 제목 / 상태 등 기본 정보 수정 (content는 지금 요구사항상 제외)
        post.update(req.getTitle(), null, req.getStatus());

        // 3) 프롬프트 내용 수정
        if (req.getPrompts() != null && !req.getPrompts().isEmpty()) {
            req.getPrompts().forEach((k, v) -> {
                if (v == null || v.isBlank()) return;

                PromptType type = switch (k.toLowerCase()) {
                    case "chatgpt" -> PromptType.GPT;
                    case "gemini" -> PromptType.GEMINI;
                    case "claude" -> PromptType.CLAUDE;
                    default -> throw new BadRequestException("UNSUPPORTED_PROMPT_TYPE", "지원하지 않는 프롬프트 타입입니다.");
                };

                // 기존 프롬프트 있으면 수정, 없으면 새로 생성
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

        // 4) 업데이트 시간 갱신
        post.touchUpdatedAt();

        return PostUpdateResponse.of(post.getId(), "게시글이 수정되었습니다.");
    }
}