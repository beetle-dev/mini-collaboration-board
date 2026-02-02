package com.minicollaborationboard.domain.ticket.entity;

import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Ticket extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private int priority;
    private LocalDateTime dueDate;
    private Long boardId;
    private Long assigneeId;

}
