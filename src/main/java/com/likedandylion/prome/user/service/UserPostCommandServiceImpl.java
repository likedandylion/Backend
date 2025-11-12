package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.DeleteMyPostsRequest;
import com.likedandylion.prome.user.dto.DeleteMyPostsResponse;
import com.likedandylion.prome.user.repository.UserPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserPostCommandService 구현체.
 * - 선택된 postIds 중 "내 글"만 삭제
 * - 삭제된 것 / 스킵된 것 구분
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserPostCommandServiceImpl implements UserPostCommandService {

    private final UserPostRepository userPostRepository;

    @Override
    public DeleteMyPostsResponse deleteMyPosts(Long userId, DeleteMyPostsRequest req) {
        // 1️⃣ 요청에서 postIds 추출
        List<Long> requestIds = req.getPostIds();

        // 2️⃣ 실제 DB에서 "내가 쓴 글" 중 존재하는 것만 조회
        List<Long> myPostIds = userPostRepository
                .findAllByUser_IdAndIdIn(userId, requestIds)
                .stream()
                .map(post -> post.getId())
                .toList();

        // 3️⃣ "삭제 가능한 글"과 "스킵될 글" 구분
        Set<Long> foundSet = Set.copyOf(myPostIds);
        List<Long> skippedIds = requestIds.stream()
                .filter(id -> !foundSet.contains(id))
                .collect(Collectors.toList());

        // 4️⃣ 실제 삭제 실행
        userPostRepository.deleteByUser_IdAndIdIn(userId, myPostIds);

        // 5️⃣ 결과 요약 DTO 반환
        return DeleteMyPostsResponse.of(myPostIds, skippedIds);
    }
}