package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "티켓 생성 요청 DTO")
public class CreateTicketReqDto {

    @NotBlank
    private String title;

    @NotNull
    private Long boardId;

    private String description;
    private TicketPriority priority;
    private LocalDateTime dueDate;
    private Long assigneeId;
}
