package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface UserPostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE u.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findAllWithUserByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    List<Post> findAllByUser_IdAndIdIn(Long userId, Collection<Long> postIds);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    long deleteByUser_IdAndIdIn(Long userId, Collection<Long> postIds);
}