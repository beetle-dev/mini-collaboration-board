package com.minicollaborationboard.domain.ticket.service;

import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.comment.dto.CommentResDto;
import com.minicollaborationboard.domain.comment.entity.Comment;
import com.minicollaborationboard.domain.comment.repository.CommentRepository;
import com.minicollaborationboard.domain.comment.service.CommentService;
import com.minicollaborationboard.domain.ticket.dto.*;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import com.minicollaborationboard.domain.ticket.repository.TicketQueryRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.global.common.service.SequenceService;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final UserService userService;
    private final BoardService boardService;
    private final TicketRepository ticketRepository;
    private final TicketQueryRepository ticketQueryRepository;
    private final SequenceService sequenceService;
    private final CommentRepository commentRepository;

    @Transactional
    public void createTicket(CreateTicketReqDto createTicketReqDto) {

        Long userId = userService.getCurrentUser().getId();
        Long boardId = createTicketReqDto.getBoardId();

        Board board = boardService.findById(boardId).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다."));

        if (!boardService.existsBoardMemberByBoardIdAndUserId(boardId, userId)) {

            throw new AccessDeniedException("티켓 생성 권한이 없습니다.");
        }

        if (createTicketReqDto.getFile() != null) {

        }

        String code = board.getCode();

        sequenceService.incrementSequence(code);
        Long newSequence = sequenceService.findLastInsertId();

        String sequence = code + "_" + newSequence;

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

        Long userId = userService.getCurrentUser().getId();
        Long boardId = ticketSearchDto.getBoardId();

        if (!boardService.existsBoardMemberByBoardIdAndUserId(boardId, userId)) {

            throw new AccessDeniedException("티켓 조회 권한이 없습니다.");
        }

        List<Ticket> tickets = ticketQueryRepository.findTickets(ticketSearchDto);

        return tickets.stream().map(this::toTicketResDto).toList();
    }

    private TicketResDto toTicketResDto(Ticket ticket) {

        List<Comment> comments = commentRepository.findAllByTicketId(ticket.getId());

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
                .comments(comments.stream().map(this::toCommentResDto).toList())
                .build();
    }

    public CommentResDto toCommentResDto(Comment comment) {

        User user = userService.findById(comment.getAuthorId());

        return CommentResDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(user.getName())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Transactional
    public void updateTicketInfo(UpdateTicketReqDto.Info updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = userService.getCurrentUser().getId();

        if (!boardService.existsBoardMemberByBoardIdAndUserId(ticket.getBoardId(), userId)) {

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

        Long userId = userService.getCurrentUser().getId();

        if (!boardService.existsBoardMemberByBoardIdAndUserId(ticket.getBoardId(), userId)) {

            throw new AccessDeniedException("티켓 수정 권한이 없습니다.");
        }

        ticket.updateTicketAssignee(updateTicketReqDto.getAssigneeId(), userId);
    }

    @Transactional
    public void updateTicketStatus(UpdateTicketReqDto.Status updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = userService.getCurrentUser().getId();

        if (!boardService.existsBoardMemberByBoardIdAndUserId(ticket.getBoardId(), userId)) {

            throw new AccessDeniedException("티켓 수정 권한이 없습니다.");
        }

        ticket.updateTicketStatus(updateTicketReqDto.getStatus(), userId);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {

        Ticket ticket = findById(ticketId);

        Long userId = userService.getCurrentUser().getId();

        BoardMemberRole role = boardService.getBoardMemberRole(userId, ticket.getBoardId());

        if (role == BoardMemberRole.MEMBER) {

            throw new AccessDeniedException("티켓 삭제 권한이 없습니다.");
        }

        commentRepository.deleteAllByTicketId(ticketId);
        ticketRepository.delete(ticket);
    }

    public Ticket findById(Long ticketId) {

        return ticketRepository.findById(ticketId).orElseThrow(() ->
                new ResourceNotFoundException("티켓을 찾을 수 없습니다."));
    }
}
