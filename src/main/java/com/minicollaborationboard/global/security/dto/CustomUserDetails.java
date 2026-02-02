package com.minicollaborationboard.global.security.dto;

import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.domain.user.entity.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@ToString
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> user.getRole().name());

        return collection;
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
