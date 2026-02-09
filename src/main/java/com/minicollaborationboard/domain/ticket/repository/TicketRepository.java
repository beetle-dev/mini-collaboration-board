package com.minicollaborationboard.domain.ticket.repository;

import com.minicollaborationboard.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    void deleteAllByBoardId(Long boardId);
}
