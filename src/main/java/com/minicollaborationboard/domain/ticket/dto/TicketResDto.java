package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TicketResDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long boardId;
    private Long assigneeId;
    private String sequence;
    private Long createdBy;
    private TicketPriority priority;
    private TicketStatus status;
}
