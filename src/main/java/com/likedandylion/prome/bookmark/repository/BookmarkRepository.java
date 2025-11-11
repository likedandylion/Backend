package com.likedandylion.prome.bookmark.repository;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
}