package com.minicollaborationboard.domain.board.dto;

import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "보드 멤버 생성 요청 DTO")
public class CreateBoardMemberReqDto {

    private Long boardId;
    private Long userId;
    private BoardMemberRole boardMemberRole;
}
