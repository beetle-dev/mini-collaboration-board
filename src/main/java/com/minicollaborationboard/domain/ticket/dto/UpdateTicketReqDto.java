package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class UpdateTicketReqDto {

    @Getter @Setter
    @NoArgsConstructor
    public static class Info {

        @NotNull
        private Long ticketId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private TicketPriority priority;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class Assignee {

        @NotNull
        private Long ticketId;

        @NotNull
        private Long assigneeId;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class Status {

        @NotNull
        private Long ticketId;

        @NotNull
        private TicketStatus status;
    }
}
