package com.minicollaborationboard.domain.auth.dto;

import com.minicollaborationboard.domain.auth.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "유저 상태 수정 요청 DTO")
public class UpdateUserStatusReqDto {

    private UserStatus userStatus;
}
