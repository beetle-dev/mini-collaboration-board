package com.minicollaborationboard.domain.ticket.repository;

import com.minicollaborationboard.domain.ticket.dto.TicketSearchDto;
import com.minicollaborationboard.domain.ticket.entity.QTicket;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.entity.TicketOrderBy;
import com.minicollaborationboard.domain.ticket.entity.TicketPriority;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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

        JPAQuery<Ticket> query = jpaQueryFactory
                .selectFrom(ticket)
                .where(
                        boardIdEqualTo(ticketSearchDto.getBoardId()),
                        ticketIdEqualTo(ticketSearchDto.getTicketId()),
                        sequenceEqualTo(ticketSearchDto.getSequence()),
                        assigneeIdEqualTo(ticketSearchDto.getAssigneeId()),
                        ticketPriorityEqualTo(ticketSearchDto.getTicketPriority())
                );

        OrderSpecifier<?> orderSpecifier = toOrderSpecifier(ticketSearchDto.getOrderBy(), ticket);
        if (orderSpecifier != null) {
            query.orderBy(orderSpecifier);
        }

        return query.fetch();
    }

    private BooleanExpression ticketPriorityEqualTo(TicketPriority ticketPriority) {
        return ticketPriority != null ? ticket.priority.eq(ticketPriority) : null;
    }

    private BooleanExpression assigneeIdEqualTo(Long assigneeId) {
        return assigneeId != null ? ticket.assigneeId.eq(assigneeId) : null;
    }

    private BooleanExpression sequenceEqualTo(String sequence) {

        return (sequence != null && !sequence.isBlank()) ? ticket.sequence.eq(sequence) : null;
    }

    private BooleanExpression ticketIdEqualTo(Long ticketId) {

        return ticketId != null ? ticket.id.eq(ticketId) : null;
    }

    private BooleanExpression boardIdEqualTo(Long boardId) {

        return boardId != null ? ticket.boardId.eq(boardId) : null;
    }

    private OrderSpecifier<?> toOrderSpecifier(TicketOrderBy orderBy, QTicket ticket) {
        if (orderBy == null) return null;
        return switch (orderBy) {
            case CREATED_AT_ASC -> ticket.createdAt.asc();
            case CREATED_AT_DESC -> ticket.createdAt.desc();
            case PRIORITY_ASC -> ticket.priority.asc();
            case PRIORITY_DESC -> ticket.priority.desc();
        };
    }
}
