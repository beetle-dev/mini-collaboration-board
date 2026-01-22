package com.minicollaborationboard.domain.ticket.entity;

import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Ticket extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private TicketStatus status;
    private int priority;
    private Date dueDate;
    private Long boardId;
    private Long assigneeId;

}
