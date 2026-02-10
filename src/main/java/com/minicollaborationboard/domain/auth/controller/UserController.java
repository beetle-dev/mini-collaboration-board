package com.minicollaborationboard.domain.auth.controller;

import com.minicollaborationboard.domain.auth.dto.SignUpReqDto;
import com.minicollaborationboard.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원 관리")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "email이 중복이 아닐 경우에만 회원을 등록합니다. email, password, name은 필수 입력값입니다.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "회원가입 성공")})
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpReqDto signUpReqDto) {

        userService.signUp(signUpReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
