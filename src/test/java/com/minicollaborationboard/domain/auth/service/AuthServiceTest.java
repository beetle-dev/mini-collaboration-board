package com.minicollaborationboard.domain.auth.service;

import com.minicollaborationboard.domain.auth.dto.SignUpReqDto;
import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    AuthService authService;

    @Test
    void 정상_회원가입() {

        //given
        String email = "test@test.com";

        SignUpReqDto signUpReqDto = new SignUpReqDto();
        signUpReqDto.setEmail(email);
        signUpReqDto.setPassword("password");
        signUpReqDto.setName("테스트");

        given(bCryptPasswordEncoder.encode(anyString())).willReturn("encodedPassword");

        //when
        authService.signUp(signUpReqDto);

        //then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User user = captor.getValue();

        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    void 이메일중복_회원가입() {

        //given
        String email = "test@test.com";

        SignUpReqDto signUpReqDto = new SignUpReqDto();
        signUpReqDto.setEmail(email);
        signUpReqDto.setPassword("password");
        signUpReqDto.setName("테스트");

        given(bCryptPasswordEncoder.encode(anyString())).willReturn("encodedPassword");

        //when
        given(userRepository.existsByEmail(email))
                .willReturn(Boolean.FALSE)
                .willReturn(Boolean.TRUE);

        authService.signUp(signUpReqDto);

        DuplicateResourceException e = assertThrows(DuplicateResourceException.class, () -> authService.signUp(signUpReqDto));

        //then
        verify(userRepository, times(1)).save(any(User.class));

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }
}