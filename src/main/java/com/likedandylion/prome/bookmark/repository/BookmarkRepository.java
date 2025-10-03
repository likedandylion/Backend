package com.likedandylion.prome.bookmark.repository;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
