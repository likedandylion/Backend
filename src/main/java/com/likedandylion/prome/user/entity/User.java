package com.likedandylion.prome.user.entity;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.reaction.entity.Reaction;
import com.likedandylion.prome.subscription.entity.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "uq_users_nickname", columnNames = "nickname")}
)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    // 프로필 이미지 URL (nullable 허용, 500자 제한)
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "provider", nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "blue_ticket")
    private int blueTicket;

    @Column(name = "green_ticket")
    private int greenTicket;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Reaction> reactions = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;

    @PrePersist
    private void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로필(닉네임/이미지URL) 변경 — 도메인 메서드
     * setter를 여기저기 열지 않고, 의도 있는 변경만 허용합니다.
     */
    public void updateProfile(String newNickname, String newProfileImageUrl) {
        this.nickname = newNickname;
        this.profileImageUrl = newProfileImageUrl;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
