package com.dev5ops.healthtart.user.domain;

import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

// security의 loadUserByUsername의 리턴 값을 바꿔주기 위한(토큰 생성을 위해) 커스텀 user
@Getter
@Setter
@AllArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {

    private UserDTO userDTO;  // UserDTO를 담는 필드
    private List<GrantedAuthority> authorities;  // 권한 정보를 담을 필드

    // 추가 필드: true/false 값을 받을 필드
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    @Override
    public String getUsername() {
        return userDTO.getUserEmail();  // 실제 유저 이메일을 반환
    }

    @Override
    public String getPassword() {
        return userDTO.getUserPassword();  // 실제 비밀번호를 반환하도록 수정
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;  // 사용자로부터 받은 값을 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;  // 사용자로부터 받은 값을 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;  // 사용자로부터 받은 값을 반환
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;  // 사용자로부터 받은 값을 반환
    }
}
