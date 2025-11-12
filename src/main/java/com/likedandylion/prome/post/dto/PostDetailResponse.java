package com.likedandylion.prome.post.dto;

import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.prompt.entity.PromptType;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        String title,
        String author,
        int views,
        int likes,
        LocalDateTime createdAt,
        List<PromptItem> prompts
) {
    public static PostDetailResponse from(Post post) {
        String authorName = post.getUser() != null ? post.getUser().getNickname() : "익명";
        int likeCount = (post.getReactions() != null) ? post.getReactions().size() : 0;

        List<PromptItem> promptItems = post.getPrompts().stream()
                .map(PromptItem::from)
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                authorName,
                post.getViews(),
                likeCount,
                post.getCreatedAt(),
                promptItems
        );
    }

    public record PromptItem(PromptType type, String content) {
        public static PromptItem from(Prompt prompt) {
            return new PromptItem(prompt.getType(), prompt.getContent());
        }
    }
}