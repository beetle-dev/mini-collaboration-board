package com.minicollaborationboard.domain.ticket.dto;

import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "티켓 생성 요청 DTO")
public class CreateTicketReqDto {

    @NotBlank
    private String title;

    @NotNull
    private Long boardId;

    private String description;
    private MultipartFile file;
    private TicketPriority priority;
    private LocalDateTime dueDate;
    private Long assigneeId;
}
