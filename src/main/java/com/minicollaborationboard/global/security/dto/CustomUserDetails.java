package com.minicollaborationboard.global.security.dto;

import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.entity.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
        );
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getEmail();
    }

    public String getUuid() {

        return user.getUuid();
    }

    @Override
    public boolean isEnabled() {

        return user.getStatus().equals(UserStatus.ACTIVE);
    }
}
