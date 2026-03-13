package com.minicollaborationboard.domain.auth.controller;

import com.minicollaborationboard.domain.auth.dto.SignUpReqDto;
import com.minicollaborationboard.domain.auth.dto.UpdateUserStatusReqDto;
import com.minicollaborationboard.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "로그인", description = "Spring Security 기본 로그인")
    @PostMapping("/login")
    public void login(
            @RequestParam String username,
            @RequestParam String password
    ) {
    }

    @Operation(summary = "유저 상태 변경", description = "유저 상태를 ACTIVE/BLOCKED/WITHDRAW 중 하나로 변경합니다. WITHDRAW 상태는 다른 상태로 변경이 불가합니다.")
    @PatchMapping("/user/{uuid}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable("uuid") String uuid,
            @RequestBody UpdateUserStatusReqDto updateUserStatusReqDto) {

        userService.updateUserStatus(uuid, updateUserStatusReqDto);

        return ResponseEntity.noContent().build();
    }
}
