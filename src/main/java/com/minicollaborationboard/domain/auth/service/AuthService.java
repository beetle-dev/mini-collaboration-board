package com.minicollaborationboard.domain.auth.service;

import com.minicollaborationboard.domain.auth.dto.SingUpReqDto;
import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.user.entity.Role;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.domain.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUp(SingUpReqDto signUpReqDto) {

        String email = signUpReqDto.getEmail();
        String password = signUpReqDto.getPassword();
        String name = signUpReqDto.getName();

        if (userRepository.existsByEmail(email)) {

            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .status(UserStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }
}
