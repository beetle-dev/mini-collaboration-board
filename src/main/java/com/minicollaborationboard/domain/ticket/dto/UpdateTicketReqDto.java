package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class UpdateTicketReqDto {

    @Getter
    public static class Info {

        @NotNull
        private Long ticketId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private TicketPriority priority;
    }

    @Getter
    public static class Assignee {

        @NotNull
        private Long ticketId;

        @NotNull
        private Long assigneeId;
    }

    @Getter
    public static class Status {

        @NotNull
        private Long ticketId;

        @NotNull
        private TicketStatus status;
    }
}
