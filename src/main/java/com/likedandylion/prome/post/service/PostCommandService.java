package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostUpdateRequest;
import com.likedandylion.prome.post.dto.PostUpdateResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.prompt.entity.Type;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;

    @Transactional
    public PostUpdateResponse update(Long postId, PostUpdateRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

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

                Type type = switch (k.toLowerCase()) {
                    case "chatgpt" -> Type.GPT;
                    case "gemini"  -> Type.GEMINI;
                    case "claude"  -> Type.CLAUDE;
                    default -> throw new IllegalArgumentException("지원하지 않는 프롬프트 타입: " + k);
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