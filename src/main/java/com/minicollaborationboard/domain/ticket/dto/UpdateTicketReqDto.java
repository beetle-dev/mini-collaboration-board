package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class UpdateTicketReqDto {

    @Getter
    public static class TicketInfoDto {

        @NotNull
        private Long ticketId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private TicketPriority priority;
    }

    @Getter
    public static class TicketAssigneeDto {

        @NotNull
        private Long ticketId;

        @NotNull
        private Long assigneeId;
    }

    @Getter
    public static class TicketStatusDto {

        @NotNull
        private Long ticketId;

        @NotNull
        private TicketStatus status;
    }
}
