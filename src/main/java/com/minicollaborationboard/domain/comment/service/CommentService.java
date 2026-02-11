package com.minicollaborationboard.domain.comment.service;

import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.comment.dto.CommentResDto;
import com.minicollaborationboard.domain.comment.dto.CreateCommentReqDto;
import com.minicollaborationboard.domain.comment.dto.UpdateCommentReqDto;
import com.minicollaborationboard.domain.comment.entity.Comment;
import com.minicollaborationboard.domain.comment.repository.CommentRepository;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.service.TicketService;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserService userService;
    private final TicketService ticketService;
    private final BoardService boardService;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(CreateCommentReqDto createCommentReqDto) {

        Long userId = userService.getCurrentUser().getId();

        String content = createCommentReqDto.getContent();
        Long ticketId = createCommentReqDto.getTicketId();

        validateBoardAndTicket(ticketId, userId);

        commentRepository.save(Comment.builder()
                        .content(content)
                        .authorId(userId)
                        .ticketId(ticketId)
                .build());
    }

    private void validateBoardAndTicket(Long ticketId, Long userId) {

        Ticket ticket = ticketService.findById(ticketId);

        Board board = boardService.findById(ticket.getBoardId()).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다.")
        );

        if (!boardService.existsBoardMemberByBoardIdAndUserId(board.getId(), userId)) {

            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentReqDto updateCommentReqDto) {

        Long userId = userService.getCurrentUser().getId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        validateBoardAndTicket(comment.getTicketId(), userId);

        if (!Objects.equals(userId, comment.getAuthorId())) {

            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        comment.updateContent(updateCommentReqDto.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {

        Long userId = userService.getCurrentUser().getId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        validateBoardAndTicket(comment.getTicketId(), userId);

        if (!Objects.equals(userId, comment.getAuthorId())) {

            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        commentRepository.deleteById(commentId);
    }
}
