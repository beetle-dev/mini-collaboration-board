package com.minicollaborationboard.domain.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Schema(description = "티켓 검색 요청 DTO")
public class TicketSearchDto {

    private Long boardId;
    private Long ticketId;
    private String sequence;
    private Long assigneeId;
}
