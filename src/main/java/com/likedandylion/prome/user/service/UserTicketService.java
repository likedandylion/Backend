package com.likedandylion.prome.user.service;

import com.likedandylion.prome.global.exception.BadRequestException;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import com.likedandylion.prome.user.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사용자 티켓(블루, 그린) 사용 로직을 전담하는 서비스
 */
@Service
@RequiredArgsConstructor
public class UserTicketService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserSubscriptionRepository userSubscriptionRepository; //

    /**
     * 게시글 조회 시 블루 티켓을 차감합니다. (JPA 변경 감지 활용)
     * @param userId 조회한 사용자 ID
     * @param postId 조회된 게시글 ID
     */
    @Transactional
    public void deductBlueTicketForView(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_POST", "게시글을 찾을 수 없습니다."));

        // 1. 본인이 작성한 글인지 확인
        // post.getUser()가 null일 수 있으니 null 체크 후 ID 비교
        if (post.getUser() != null && post.getUser().getId().equals(userId)) {
            return; // 본인 글은 차감하지 않음
        }

        // 2. 프리미엄 구독자인지 확인
        // UserSubscriptionRepository의 쿼리를 사용하여 활성 구독 확인
        boolean isPremium = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, LocalDateTime.now())
                .isPresent();
        if (isPremium) {
            return; // 프리미엄 회원은 차감하지 않음
        }

        // 3. 티켓이 충분한지 확인 후 차감
        if (user.getBlueTicket() <= 0) {
            // 프론트엔드(PromptDetail.jsx)에서 이미 이 예외를 예상하고 있습니다.
            throw new BadRequestException("NO_BLUE_TICKETS", "블루 티켓이 부족하여 게시글을 열람할 수 없습니다.");
        }

        // User 엔티티의 addTickets 메서드를 사용하여 티켓 차감
        user.addTickets(-1, 0);
        // @Transactional이 적용되어 있어 userRepository.save(user)를 호출할 필요가 없습니다. (변경 감지)
    }

    /**
     * 프롬프트 복사 시 그린 티켓을 차감합니다. (JPA 변경 감지 활용)
     * @param userId 복사한 사용자 ID
     * @param postId 복사한 프롬프트가 속한 게시글 ID
     */
    @Transactional
    public void deductGreenTicketForCopy(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));

        // 1. 프리미엄 구독자인지 확인
        boolean isPremium = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, LocalDateTime.now())
                .isPresent();
        if (isPremium) {
            return; // 프리미엄 회원은 차감하지 않음
        }

        // 2. 티켓이 충분한지 확인 후 차감
        if (user.getGreenTicket() <= 0) {
            // 프론트엔드(PromptDetail.jsx)에서 이미 이 예외를 예상하고 있습니다.
            throw new BadRequestException("NO_GREEN_TICKETS", "그린 티켓이 부족하여 복사할 수 없습니다.");
        }

        // User 엔티티의 addTickets 메서드를 사용하여 티켓 차감
        user.addTickets(0, -1);
    }
}