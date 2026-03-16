package com.minicollaborationboard.domain.comment.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.comment.dto.CreateCommentReqDto;
import com.minicollaborationboard.domain.comment.dto.UpdateCommentReqDto;
import com.minicollaborationboard.domain.comment.entity.Comment;
import com.minicollaborationboard.domain.comment.repository.CommentRepository;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.service.TicketService;
import com.minicollaborationboard.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserService userService;
    private final TicketService ticketService;
    private final BoardService boardService;
    private final CommentRepository commentRepository;
    private final AuthService authService;

    @Transactional
    public void createComment(CreateCommentReqDto createCommentReqDto) {

        Long userId = userService.getCurrentUser().getId();

        String content = createCommentReqDto.getContent();
        Long ticketId = createCommentReqDto.getTicketId();

        validateBoardAndTicket(ticketId);

        commentRepository.save(Comment.builder()
                        .content(content)
                        .authorId(userId)
                        .ticketId(ticketId)
                .build());
    }

    private void validateBoardAndTicket(Long ticketId) {

        Ticket ticket = ticketService.findById(ticketId);

        Board board = boardService.findById(ticket.getBoardId()).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다.")
        );

        authService.validateAccessPermission(board.getId());
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentReqDto updateCommentReqDto) {


        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        validateBoardAndTicket(comment.getTicketId());

        authService.validateUpdateCommentPermission(comment.getAuthorId());

        comment.updateContent(updateCommentReqDto.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {


        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        validateBoardAndTicket(comment.getTicketId());

        authService.validateUpdateCommentPermission(comment.getAuthorId());

        commentRepository.deleteById(commentId);
    }
}
