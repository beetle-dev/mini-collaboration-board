package com.minicollaborationboard.domain.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTicketReqDto {

    private Long boardId;
    private Long ticketId;
    private String sequence;
    private Long assigneeId;
}
