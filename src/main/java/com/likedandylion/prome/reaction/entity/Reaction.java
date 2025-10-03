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
        name = "reactions",
        uniqueConstraints = {
                // 같은 사용자·같은 대상·같은 종류의 반응은 1개만 허용
                @UniqueConstraint(name = "uq_user_post_type", columnNames = {"user_id", "post_id", "reaction_type"}),
                @UniqueConstraint(name = "uq_user_comment_type", columnNames = {"user_id", "comment_id", "reaction_type"})
        }
)
@org.hibernate.annotations.Check(
        constraints =
                "(" +
                        "(post_id IS NOT NULL AND comment_id IS NULL) OR " +   // 게시글 반응
                        "(post_id IS NULL AND comment_id IS NOT NULL)" +       // 댓글 반응
                        ")"
)
public class Reaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private Type type;
}
