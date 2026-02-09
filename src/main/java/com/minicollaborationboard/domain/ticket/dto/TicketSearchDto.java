package com.minicollaborationboard.domain.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class TicketSearchDto {

    private Long boardId;
    private Long ticketId;
    private String sequence;
    private Long assigneeId;
}
