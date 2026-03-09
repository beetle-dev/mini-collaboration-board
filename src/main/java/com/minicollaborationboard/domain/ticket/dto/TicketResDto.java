package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.comment.dto.CommentResDto;
import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "티켓 응답 DTO")
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

    @Setter
    private List<CommentResDto> comments;
}
