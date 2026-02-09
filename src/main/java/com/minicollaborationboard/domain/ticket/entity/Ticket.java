package com.minicollaborationboard.domain.ticket.entity;

import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Long boardId;

    private Long assigneeId;

    @Column(unique = true)
    private String sequence;

    private Long createdBy;
    private Long updatedBy;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public void updateTicketInfo(String title, String description, LocalDateTime dueDate, TicketPriority priority, Long userId) {

        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.updatedBy = userId;
    }

    public void updateTicketAssignee(Long assigneeId, Long userId) {

        this.assigneeId = assigneeId;
        this.updatedBy = userId;
    }

    public void updateTicketStatus(TicketStatus status, Long userId) {

        this.status = status;
        this.updatedBy = userId;
    }
}
