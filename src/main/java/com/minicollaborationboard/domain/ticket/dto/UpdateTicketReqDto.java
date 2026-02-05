package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateTicketReqDto {

    @NotNull
    private Long id;

    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long assigneeId;
    private TicketPriority priority;
    private TicketStatus status;
}
