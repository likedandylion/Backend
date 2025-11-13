package com.likedandylion.prome.global.security;

import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // N+1 문제
        // User와 Subscription을 한번에 가져옴.
        User user = userRepository.findByLoginIdWithSubscription(loginId)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + loginId));

        return new CustomUserDetails(user);
    }
}