package com.minicollaborationboard.domain.board.dto;

import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import lombok.Getter;

@Getter
public class CreateInviReqDto {

    private String inviteeEmail;
    private Long boardId;
    private BoardMemberRole role;
}
