package com.minicollaborationboard.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignUpReqDto {

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    @Size(min = 8)
    @Pattern(regexp = ".*[\\p{P}\\p{S}].*",
            message = "비밀번호에 특수문자를 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotEmpty
    private String name;
}
