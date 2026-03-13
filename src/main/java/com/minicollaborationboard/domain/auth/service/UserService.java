package com.minicollaborationboard.domain.auth.service;

import com.minicollaborationboard.domain.auth.dto.SignUpReqDto;
import com.minicollaborationboard.domain.auth.dto.UpdateUserStatusReqDto;
import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.auth.entity.Role;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.entity.UserStatus;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findById(Long userId) {

        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("유저를 찾을 수 없습니다."));
    }
    
    @Transactional
    public void updateUserStatus(String uuid, UpdateUserStatusReqDto updateUserStatusReqDto) {

        User currentUser = getCurrentUser();
        Long currentUserId = currentUser.getId();

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다. uuid = " + uuid));

        Long targetUserId = user.getId();
        UserStatus currentUserStatus = user.getStatus();
        UserStatus changeTo = updateUserStatusReqDto.getUserStatus();

        if (currentUserStatus == UserStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴한 회원의 상태를 변경할 수 없습니다.");
        }

        if (changeTo == UserStatus.WITHDRAWN) {
            if (currentUserStatus != UserStatus.ACTIVE || !currentUserId.equals(targetUserId)) {
                throw new IllegalArgumentException("WITHDRAW 상태로 변경할 수 있는 권한이 없습니다.");
            }

            user.updateUserStatus(UserStatus.WITHDRAWN);
            return;
        }

        if (changeTo == UserStatus.BLOCKED) {
            if (currentUserStatus != UserStatus.ACTIVE) {
                throw new IllegalArgumentException("ACTIVE 상태의 회원만 BLOCKED 로 변경할 수 있습니다.");
            }
            // TODO 관리자(Role.ADMIN)가 ACTIVE 상태의 유저를 BLOCKED 로 변경할 수 있도록 Role 체크 로직 추가

            user.updateUserStatus(UserStatus.BLOCKED);
            return;
        }

        if (changeTo == UserStatus.ACTIVE) {
            if (currentUserStatus != UserStatus.BLOCKED) {
                throw new IllegalArgumentException("BLOCKED 상태의 회원만 ACTIVE 로 변경할 수 있습니다.");
            }
            // TODO 관리자(Role.ADMIN)가 BLOCKED 상태의 유저를 ACTIVE 로 변경할 수 있도록 Role 체크 로직 추가

            user.updateUserStatus(UserStatus.ACTIVE);
        }
    }
}
