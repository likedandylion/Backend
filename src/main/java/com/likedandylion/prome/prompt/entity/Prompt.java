package com.likedandylion.prome.prompt.entity;

import com.likedandylion.prome.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "prompts")
public class Prompt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PromptType type;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    public void changeContent(String content) { this.content = content; }

    public Prompt(Post post, PromptType type, String content) {
        this.post = post;
        this.type = type;
        this.content = content;
    }
}