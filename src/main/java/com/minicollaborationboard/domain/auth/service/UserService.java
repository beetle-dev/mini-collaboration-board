package com.minicollaborationboard.domain.auth.service;

import com.minicollaborationboard.domain.auth.dto.SignUpReqDto;
import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.auth.entity.Role;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.entity.UserStatus;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUp(SignUpReqDto signUpReqDto) {

        String email = signUpReqDto.getEmail();
        String password = signUpReqDto.getPassword();
        String name = signUpReqDto.getName();

        if (userRepository.existsByEmail(email)) {

            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .status(UserStatus.ACTIVE)
                .role(Role.USER)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
    }

    public User getCurrentUser() {

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        String userUuid = user.getUuid();

        return userRepository.findByUuid(userUuid).orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. uuid = " + userUuid));
    }

    public Optional<User> findByEmail(String inviteeEmail) {

        return userRepository.findByEmail(inviteeEmail);
    }
}
