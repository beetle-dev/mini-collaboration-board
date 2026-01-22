package com.minicollaborationboard.global.security.service;

import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user != null) {

            return new CustomUserDetails(user);
        }

        return null;
    }
}
