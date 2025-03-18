package org.dfbf.soundlink.global.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails{
    private final Long userId;

    public CustomUserDetails(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); //사용자의 권한 정보 반환
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);//로그인할때 ID
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    //계정 만료 여부(만료 x)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;    //계정이 잠겨있는지 확인.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    //비밀번호 유효기간 확인
    }

    @Override
    public boolean isEnabled() {
        return true;    //계정활성화 여부 확인
    }
}
