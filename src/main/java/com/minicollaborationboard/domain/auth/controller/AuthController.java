package com.minicollaborationboard.domain.auth.controller;

import com.minicollaborationboard.domain.auth.dto.SingUpReqDto;
import com.minicollaborationboard.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(SingUpReqDto signUpReqDto) {

        authService.signUp(signUpReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
