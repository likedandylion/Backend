package com.likedandylion.prome.user.service;

import com.likedandylion.prome.comment.entity.Comment;
import com.likedandylion.prome.user.dto.DeleteMyCommentsRequest;
import com.likedandylion.prome.user.dto.DeleteMyCommentsResponse;
import com.likedandylion.prome.user.repository.UserCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * "내가 쓴 댓글 선택 삭제" 서비스 구현체
 * 흐름:
 * 1. 요청받은 commentIds 중에서 "내 댓글"만 조회
 * 2. 실제 삭제 실행
 * 3. 삭제된 목록 / 스킵된 목록 / 개수로 응답 DTO 구성
 */
@Service
@RequiredArgsConstructor
public class UserCommentCommandServiceImpl implements UserCommentCommandService {

    private final UserCommentRepository userCommentRepository;

    @Override
    public DeleteMyCommentsResponse deleteMyComments(Long userId, DeleteMyCommentsRequest request) {
        List<Long> requestedIds = request.getCommentIds();

        // 1️. 요청 ID 중 내 댓글만 필터링
        List<Comment> myComments = userCommentRepository.findAllByUser_IdAndIdIn(userId, requestedIds);
        Set<Long> myCommentIds = myComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());

        // 2️. 실제 삭제 (DB 반영)
        long deletedCount = userCommentRepository.deleteByUser_IdAndIdIn(userId, myCommentIds);

        // 3️. 삭제되지 않은 ID(= 존재X or 남의 댓글)
        List<Long> skippedIds = new ArrayList<>(requestedIds);
        skippedIds.removeAll(myCommentIds);

        // 4️. 응답 구성
        return new DeleteMyCommentsResponse(
                new ArrayList<>(myCommentIds), // deletedIds
                skippedIds,                    // skippedIds
                (int) deletedCount             // deletedCount
        );
    }
}
