package com.likedandylion.prome.bookmark.service;

import com.likedandylion.prome.bookmark.dto.BookmarkToggleResponse;
import com.likedandylion.prome.bookmark.dto.MyBookmarkResponse;
import com.likedandylion.prome.bookmark.entity.Bookmark;
import com.likedandylion.prome.bookmark.repository.BookmarkRepository;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 북마크 추가 / 삭제 토글
     */
    @Transactional
    public BookmarkToggleResponse toggle(Long postId, Long userId) {

        Bookmark existing = bookmarkRepository
                .findByUserIdAndPostId(userId, postId)
                .orElse(null);

        // 이미 있으면 삭제 = 북마크 해제
        if (existing != null) {
            bookmarkRepository.delete(existing);
            return new BookmarkToggleResponse(postId, false);
        }

        // 없으면 새로 추가
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Bookmark bookmark = new Bookmark(user, post);
        bookmarkRepository.save(bookmark);

        return new BookmarkToggleResponse(postId, true);
    }

    /**
     * 내 북마크 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MyBookmarkResponse> getMyBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return bookmarks.stream()
                .map(MyBookmarkResponse::from)
                .toList();
    }
}