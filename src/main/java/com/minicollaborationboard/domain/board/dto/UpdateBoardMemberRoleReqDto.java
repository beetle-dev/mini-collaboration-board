package com.minicollaborationboard.domain.board.dto;

import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "보드 멤버 권한 수정 요청 DTO")
public class UpdateBoardMemberRoleReqDto {

    private Long boardMemberId;
    private BoardMemberRole changeTo;
}
