package com.minicollaborationboard.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignUpReqDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private String name;
}
