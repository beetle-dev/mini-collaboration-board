package com.minicollaborationboard.global.security.service;

import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.entity.UserStatus;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        UserStatus status = user.getStatus();

        if (status != UserStatus.ACTIVE) {

            throw new BadCredentialsException("활성화된 유저가 아닙니다.");
        }

        return new CustomUserDetails(user);
    }
}
