package com.likedandylion.prome.post.entity;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "views", nullable = false)
    private int views;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prompt> prompts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> reactions = new ArrayList<>();

    public Post(User user, String title, Status status) {
        this.user = user;
        this.title = title;
        this.status = status;
        this.views = 0;
    }

    public void updateTitle(String title) { this.title = title; }
    public void updateStatus(Status status) { this.status = status; }
    public void addPrompt(Prompt prompt) { this.prompts.add(prompt); }
    public void removeAllPrompts() { this.prompts.clear(); }
    public void touchUpdatedAt() { this.updatedAt = LocalDateTime.now(); }

    public Optional<Prompt> findPromptByType(Enum<?> type) {
        return prompts.stream().filter(p -> p.getType() == type).findFirst();
    }

    public void updateTitle(String title) { this.title = title; }
    public void updateStatus(Status status) { this.status = status; }
    public void addPrompt(Prompt prompt) { this.prompts.add(prompt); }
    public void removeAllPrompts() { this.prompts.clear(); }
    public void touchUpdatedAt() { this.updatedAt = LocalDateTime.now(); }

    public Optional<Prompt> findPromptByType(Enum<?> type) {
        return prompts.stream().filter(p -> p.getType() == type).findFirst();
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.ACTIVE; // 기본 상태
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}