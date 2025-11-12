package com.likedandylion.prome.reaction.entity;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_post_like",
                        columnNames = {"user_id", "post_id"}
                ),
                @UniqueConstraint(
                        name = "uq_user_comment_like",
                        columnNames = {"user_id", "comment_id"}
                )
        }
)
@org.hibernate.annotations.Check(
        constraints =
                "(" +
                        "(post_id IS NOT NULL AND comment_id IS NULL) OR " +
                        "(post_id IS NULL AND comment_id IS NOT NULL)" +
                        ")"
)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Like(User user, Post post, Comment comment) {
        this.user = user;
        this.post = post;
        this.comment = comment;
    }
}