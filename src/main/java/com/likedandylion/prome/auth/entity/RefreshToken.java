package com.likedandylion.prome.auth.entity;

import com.likedandylion.prome.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_token_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_refresh_token_token", columnNames = "token_hash")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long refreshId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant revokedAt;

    @Builder
    public RefreshToken(User user, String tokenHash, Instant expiresAt, Instant createdAt, Instant revokedAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
    }

    public boolean isExpired() { return expiresAt.isBefore(Instant.now()); }
    public boolean isRevoked() { return revokedAt != null; }
    public void revokeNow()    { this.revokedAt = Instant.now(); }
    public void rotateTo(String newHash, Instant newExpiresAt) {
        this.tokenHash = newHash;
        this.expiresAt = newExpiresAt;
        this.createdAt = Instant.now();
        this.revokedAt = null;
    }
}