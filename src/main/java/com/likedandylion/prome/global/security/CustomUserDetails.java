package com.likedandylion.prome.global.security;

import com.likedandylion.prome.user.entity.Role;
import com.likedandylion.prome.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String loginId;
    private final String password;
    private final Role role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() {return loginId;} // 스프링 시큐리티가 로그인 아이디로 쓰는 값

    // 계정 만료/잠금/비밀번호 만료 → 프로젝트 정책에 맞게 처리
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
