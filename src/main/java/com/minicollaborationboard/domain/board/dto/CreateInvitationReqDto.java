package com.minicollaborationboard.domain.board.dto;

import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "보드 초대 생성 요청 DTO")
public class CreateInvitationReqDto {

    @NotEmpty
    @Email
    private String inviteeEmail;

    @NotNull
    private BoardMemberRole role;
}
