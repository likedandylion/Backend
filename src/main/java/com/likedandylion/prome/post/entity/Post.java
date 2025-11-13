package com.likedandylion.prome.post.entity;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.prompt.entity.Prompt;
import com.likedandylion.prome.reaction.entity.Like;
import com.likedandylion.prome.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 제목
    @Column(nullable = false)
    private String title;

    // 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 카테고리 (선택)
    @Column(length = 100)
    private String category;

    // 태그 리스트 (별도 테이블 post_tags)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // 상태 (ACTIVE / DELETED 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    // 생성/수정 시각
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 조회수 (DB 컬럼: view_count)
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    // 좋아요 수
    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    // 댓글 수
    @Builder.Default
    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    // 북마크 수
    @Builder.Default
    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;

    // 공유 수
    @Builder.Default
    @Column(name = "share_count", nullable = false)
    private int shareCount = 0;

    // 프롬프트 리스트
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prompt> prompts = new ArrayList<>();

    // 좋아요 엔티티 리스트
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // 댓글 리스트
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 북마크 리스트
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // 저장 전 호출
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 전 호출
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 제목 / 내용 / 상태 수정
    public void update(String title, String content, Status status) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (status != null) {
            this.status = status;
        }
    }

    // 연관관계 편의 메서드
    public void addPrompt(Prompt prompt) {
        this.prompts.add(prompt);
    }

    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}