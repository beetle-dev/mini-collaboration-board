package com.minicollaborationboard.domain.ticket.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.ticket.dto.*;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import com.minicollaborationboard.domain.ticket.repository.TicketQueryRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final AuthService authService;
    private final BoardService boardService;
    private final TicketRepository ticketRepository;
    private final TicketQueryRepository ticketQueryRepository;

    @Transactional
    public void createTicket(CreateTicketReqDto createTicketReqDto) {

        Long userId = authService.getCurrentUser().getId();
        Long boardId = createTicketReqDto.getBoardId();

        Board board = boardService.findById(boardId).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다."));

        boardService.increaseLastTicketSequence(boardId);
        Long ticketSequence = boardService.getLastTicketSequence(boardId);

        String code = board.getCode();

        String sequence = code + "_" + ticketSequence;

        ticketRepository.save(Ticket.builder()
                        .title(createTicketReqDto.getTitle())
                        .description(createTicketReqDto.getDescription())
                        .dueDate(createTicketReqDto.getDueDate())
                        .boardId(boardId)
                        .assigneeId(createTicketReqDto.getAssigneeId())
                        .sequence(sequence)
                        .createdBy(userId)
                        .priority(createTicketReqDto.getPriority())
                        .status(TicketStatus.TODO)
                .build());
    }

    @Transactional(readOnly = true)
    public List<TicketResDto> getTickets(TicketSearchDto ticketSearchDto) {

        List<Ticket> tickets = ticketQueryRepository.findTickets(ticketSearchDto);

        return tickets.stream().map(this::toTicketResDto).toList();
    }

    private TicketResDto toTicketResDto(Ticket ticket) {

        return TicketResDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .dueDate(ticket.getDueDate())
                .boardId(ticket.getBoardId())
                .assigneeId(ticket.getAssigneeId())
                .sequence(ticket.getSequence())
                .createdBy(ticket.getCreatedBy())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .build();
    }

    @Transactional
    public void updateTicketInfo(UpdateTicketReqDto.Info updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = authService.getCurrentUser().getId();

        if (!boardService.exsistByUserIdAndBoardId(ticket.getBoardId(), userId)) {

            throw new AccessDeniedException("티켓 수정 권한이 없습니다.");
        }

        ticket.updateTicketInfo(
                updateTicketReqDto.getTitle(),
                updateTicketReqDto.getDescription(),
                updateTicketReqDto.getDueDate(),
                updateTicketReqDto.getPriority(),
                userId);
    }

    @Transactional
    public void updateTicketAssignee(UpdateTicketReqDto.Assignee updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = authService.getCurrentUser().getId();

        if (!boardService.exsistByUserIdAndBoardId(ticket.getBoardId(), userId)) {

            throw new AccessDeniedException("티켓 수정 권한이 없습니다.");
        }

        ticket.updateTicketAssignee(updateTicketReqDto.getAssigneeId(), userId);
    }

    @Transactional
    public void updateTicketStatus(UpdateTicketReqDto.Status updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = authService.getCurrentUser().getId();

        if (!boardService.exsistByUserIdAndBoardId(ticket.getBoardId(), userId)) {

            throw new AccessDeniedException("티켓 수정 권한이 없습니다.");
        }

        ticket.updateTicketStatus(updateTicketReqDto.getStatus(), userId);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {

        Ticket ticket = findById(ticketId);

        Long userId = authService.getCurrentUser().getId();

        BoardMemberRole role = boardService.getBoardMemberRole(userId, ticket.getBoardId());

        if (role == BoardMemberRole.MEMBER) {

            throw new AccessDeniedException("티켓 삭제 권한이 없습니다.");
        }

        ticketRepository.delete(ticket);
    }

    public Ticket findById(Long ticketId) {

        return ticketRepository.findById(ticketId).orElseThrow(() ->
                new ResourceNotFoundException("티켓을 찾을 수 없습니다."));
    }

    public void deleteTicketAllByBoardId(Long boardId) {

        ticketRepository.deleteAllByBoardId(boardId);
    }
}
