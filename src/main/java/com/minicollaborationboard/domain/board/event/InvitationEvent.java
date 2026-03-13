package com.minicollaborationboard.domain.board.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationEvent {

    private Long boardId;
    private String inviteeEmail;
    private String invitationUuid;
}
