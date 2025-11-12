package com.likedandylion.prome.comment.repository;

import com.likedandylion.prome.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * N+1 문제를 해결하기 위해 Fetch Join을 사용합니다.
     * 댓글 조회 시, 댓글 작성자(User)와 반응(Reactions) 정보를 한 번의 쿼리로 함께 가져옵니다.
     */
    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user u " +
            "LEFT JOIN FETCH c.reactions r " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostIdWithUserAndReactions(@Param("postId") Long postId);
}