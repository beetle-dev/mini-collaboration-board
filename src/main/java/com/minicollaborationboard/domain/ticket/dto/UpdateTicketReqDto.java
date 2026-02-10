package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "티켓 수정 요청 DTO")
public class UpdateTicketReqDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "티켓 정보 수정 요청 DTO")
    public static class Info {

        @NotNull
        private Long ticketId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private TicketPriority priority;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "티켓 담당자 수정 요청 DTO")
    public static class Assignee {

        @NotNull
        private Long ticketId;

        @NotNull
        private Long assigneeId;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "티켓 상태 수정 요청 DTO")
    public static class Status {

        @NotNull
        private Long ticketId;

        @NotNull
        private TicketStatus status;
    }
}
