package com.minicollaborationboard.domain.ticket.service;

import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.comment.dto.CommentResDto;
import com.minicollaborationboard.domain.comment.entity.Comment;
import com.minicollaborationboard.domain.comment.repository.CommentRepository;
import com.minicollaborationboard.domain.ticket.dto.*;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.entity.TicketStatus;
import com.minicollaborationboard.domain.ticket.repository.TicketQueryRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.common.service.SequenceService;
import com.minicollaborationboard.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final UserService userService;
    private final BoardService boardService;
    private final TicketRepository ticketRepository;
    private final TicketQueryRepository ticketQueryRepository;
    private final SequenceService sequenceService;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional
    public void createTicket(CreateTicketReqDto createTicketReqDto) {

        Long userId = userService.getCurrentUser().getId();
        Long boardId = createTicketReqDto.getBoardId();

        Board board = boardService.findById(boardId).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다."));

        authService.validateAccessPermission(boardId);

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

        authService.validateAccessPermission(ticketSearchDto.getBoardId());

        List<Ticket> tickets = ticketQueryRepository.findTickets(ticketSearchDto);
        List<Long> ticketIds = tickets.stream().map(Ticket::getId).toList();

        List<Comment> comments = commentRepository.findAllByTicketIdIn(ticketIds);
        Map<Long, List<Comment>> commentByTicketId = comments.stream()
                .collect(Collectors.groupingBy(Comment::getTicketId));

        Set<Long> authorIds = comments.stream().map(Comment::getAuthorId).collect(Collectors.toSet());
        Map<Long, String> authorByUserId = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        List<TicketResDto> ticketResDtos = new ArrayList<>();
        for (Ticket ticket : tickets) {

            TicketResDto ticketResDto = TicketResDto.builder()
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

            List<Comment> commentList = commentByTicketId.getOrDefault(ticket.getId(), List.of());

            List<CommentResDto> commentResDtos = new ArrayList<>();

            for (Comment comment : commentList) {

                CommentResDto commentResDto = CommentResDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .author(authorByUserId.getOrDefault(comment.getAuthorId(), ""))
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build();

                commentResDtos.add(commentResDto);
            }

            ticketResDto.setComments(commentResDtos);

            ticketResDtos.add(ticketResDto);
        }

        return ticketResDtos;
    }

    @Transactional
    public void updateTicketInfo(UpdateTicketReqDto.Info updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = userService.getCurrentUser().getId();

        authService.validateAccessPermission(ticket.getBoardId());

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

        authService.validateAccessPermission(ticket.getBoardId());

        ticket.updateTicketAssignee(updateTicketReqDto.getAssigneeId(), userId);
    }

    @Transactional
    public void updateTicketStatus(UpdateTicketReqDto.Status updateTicketReqDto) {

        Ticket ticket = findById(updateTicketReqDto.getTicketId());

        Long userId = userService.getCurrentUser().getId();

        authService.validateAccessPermission(ticket.getBoardId());

        ticket.updateTicketStatus(updateTicketReqDto.getStatus(), userId);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {

        Ticket ticket = findById(ticketId);

        authService.validateUpdatePermission(ticket.getBoardId());

        commentRepository.deleteAllByTicketId(ticketId);
        ticketRepository.delete(ticket);
    }

    public Ticket findById(Long ticketId) {

        return ticketRepository.findById(ticketId).orElseThrow(() ->
                new ResourceNotFoundException("티켓을 찾을 수 없습니다."));
    }
}
