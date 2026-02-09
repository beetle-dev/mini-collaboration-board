package com.minicollaborationboard.domain.ticket.repository;

import com.minicollaborationboard.domain.ticket.dto.TicketSearchDto;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.minicollaborationboard.domain.ticket.entity.QTicket.ticket;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Ticket> findTickets(TicketSearchDto ticketSearchDto) {

        return jpaQueryFactory
                .selectFrom(ticket)
                .where(
                        boardIdEqualTo(ticketSearchDto.getBoardId()),
                        ticketIdEqualTo(ticketSearchDto.getTicketId()),
                        sequenceEqualTo(ticketSearchDto.getSequence()),
                        assigneeIdEqualTo(ticketSearchDto.getAssigneeId())
                )
                .fetch();
    }

    private BooleanExpression assigneeIdEqualTo(Long assigneeId) {
        return assigneeId != null ? ticket.assigneeId.eq(assigneeId) : null;
    }

    private BooleanExpression sequenceEqualTo(String sequence) {

        return !sequence.isBlank() ? ticket.sequence.eq(sequence) : null;
    }

    private BooleanExpression ticketIdEqualTo(Long ticketId) {

        return ticketId != null ? ticket.id.eq(ticketId) : null;
    }

    private BooleanExpression boardIdEqualTo(Long boardId) {

        return boardId != null ? ticket.boardId.eq(boardId) : null;
    }
}
