package com.minicollaborationboard.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationEventReqDto {

    private Long boardId;
    private String inviteeEmail;
}
