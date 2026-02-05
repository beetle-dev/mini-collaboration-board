package com.minicollaborationboard.domain.ticket.entity;

import com.minicollaborationboard.domain.ticket.dto.UpdateTicketReqDto;
import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

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
    private long updatedBy;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public void updateTicketInfo(UpdateTicketReqDto.TicketInfoDto updateTicketReqDto, Long userId) {

        this.title = updateTicketReqDto.getTitle();
        this.description = updateTicketReqDto.getDescription();
        this.dueDate = updateTicketReqDto.getDueDate();
        this.priority = updateTicketReqDto.getPriority();
        this.updatedBy = userId;
    }

    public void updateTicketAsignee(UpdateTicketReqDto.TicketAssigneeDto updateTicketReqDto, Long userId) {

        this.assigneeId = updateTicketReqDto.getAssigneeId();
        this.updatedBy = userId;
    }

    public void updateTicketStatus(UpdateTicketReqDto.TicketStatusDto updateTicketReqDto, Long userId) {

        this.status = updateTicketReqDto.getStatus();
        this.updatedBy = userId;
    }
}
