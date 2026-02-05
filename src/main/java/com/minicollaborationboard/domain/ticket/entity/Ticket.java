package com.minicollaborationboard.domain.ticket.entity;

import com.minicollaborationboard.domain.ticket.dto.UpdateTicketReqDto;
import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Ticket extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long boardId;
    private Long assigneeId;
    private String sequence;
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public void update(UpdateTicketReqDto updateTicketReqDto) {

        this.title = updateTicketReqDto.getTitle();
        this.description = updateTicketReqDto.getDescription();
        this.dueDate = updateTicketReqDto.getDueDate();
        this.assigneeId = updateTicketReqDto.getAssigneeId();
        this.priority = updateTicketReqDto.getPriority();
        this.status = updateTicketReqDto.getStatus();
    }
}
