package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketOrderBy;
import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@Getter @Setter
@NoArgsConstructor
@Schema(description = "티켓 검색 요청 DTO")
public class TicketSearchDto {

    private Long boardId;
    private Long ticketId;
    private String sequence;
    private Long assigneeId;
    private TicketPriority ticketPriority;

    private TicketOrderBy orderBy;
}
