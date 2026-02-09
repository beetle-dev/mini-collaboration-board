package com.minicollaborationboard.domain.board.dto;

import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "보드 초대 생성 요청 DTO")
public class CreateInvitationReqDto {

    @NotBlank
    private String inviteeEmail;

    @NotBlank
    private BoardMemberRole role;
}
