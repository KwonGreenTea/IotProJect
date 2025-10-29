package com.iot.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 하드코딩된 테스트 사용자 정보
        String password;
        String roleName;

        switch (username) {
            case "admin":
                password = "{noop}1234"; // {noop} = 평문 비밀번호
                roleName = "ROLE_ADMIN";
                break;
            case "user":
                password = "{noop}1234";
                roleName = "ROLE_USER";
                break;
            default:
                throw new UsernameNotFoundException("User not found: " + username);
        }

        // 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));

        // User 객체 생성
        return new User(username, password, authorities);
    }
}
