package com.minicollaborationboard.domain.board.service;

import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.dto.*;
import com.minicollaborationboard.domain.board.entity.*;
import com.minicollaborationboard.domain.board.repository.BoardInvitationRepository;
import com.minicollaborationboard.domain.board.repository.BoardMemberRepository;
import com.minicollaborationboard.domain.board.repository.BoardRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.global.common.service.SequenceService;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import com.minicollaborationboard.global.exception.ExpiredResourceException;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardInvitationRepository boardInvitationRepository;
    private final TicketRepository ticketRepository;
    private final SequenceService sequenceService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final int BOARD_INVITATION_EXPIRE_DAY = 3;

    @Transactional
    public void createBoard(CreateBoardReqDto createBoardReqDto) {

        Long userId = userService.getCurrentUser().getId();
        String boardName = createBoardReqDto.getName();
        String boardCode = createBoardReqDto.getCode();

        if (boardRepository.existsByName(boardName)) {

            throw new DuplicateResourceException("이미 존재하는 보드명 입니다.");
        }

        Board board = Board.builder()
                .name(boardName)
                .code(boardCode)
                .ownerId(userId)
                .build();

        Long boardId = boardRepository.save(board).getId();

        createBoardMember(CreateBoardMemberReqDto.builder()
                .boardId(boardId)
                .boardMemberRole(BoardMemberRole.OWNER)
                .userId(userId)
                .build());

        sequenceService.createSequence(boardCode);
    }

    @Transactional
    public void createBoardMember(CreateBoardMemberReqDto boardMemberReqDto) {

        BoardMemberRole role = boardMemberReqDto.getBoardMemberRole();
        Long boardId = boardMemberReqDto.getBoardId();
        Long userId = boardMemberReqDto.getUserId();

        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, userId)) {

            return;
        }

        boardMemberRepository.save(BoardMember.builder()
                .role(role)
                .boardId(boardId)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .build());
    }

    public Page<BoardResDto> getBoards(Long boardId, Pageable pageable) {

        Page<Board> boards = boardRepository.findBoards(boardId, pageable);

        return boards.map(this::toBoardResDto);
    }

    private BoardResDto toBoardResDto(Board board) {

        return BoardResDto.builder()
                .id(board.getId())
                .name(board.getName())
                .ownerId(board.getOwnerId())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    @Transactional
    public void createInvitation(Long boardId, CreateInvitationReqDto createInvitationReqDto) {

        Long currentUserId = userService.getCurrentUser().getId();
        BoardMember currentBoardMember = boardMemberRepository.findByUserIdAndBoardId(currentUserId, boardId)
                .orElseThrow(() -> new ResourceNotFoundException("본인이 속하지 않은 보드에 초대할 수 없습니다."));

        BoardMemberRole currentBoardMemberRole = currentBoardMember.getRole();
        BoardMemberRole inviteeBoardMemberRole = createInvitationReqDto.getRole();

        if (currentBoardMemberRole == BoardMemberRole.MEMBER ||
                (currentBoardMemberRole == BoardMemberRole.ADMIN && inviteeBoardMemberRole == BoardMemberRole.ADMIN)) {

            throw new AccessDeniedException("초대 권한이 없습니다.");
        }

        String inviteeEmail = createInvitationReqDto.getInviteeEmail();

        if (boardInvitationRepository.existsByBoardIdAndInviteeEmailAndStatus(boardId, inviteeEmail, BoardInvitationStatus.PENDING)) {

            throw new DuplicateResourceException("해당 보드에 이미 초대된 유저입니다.");
        }

        boardInvitationRepository.save(BoardInvitation.builder()
                .boardId(boardId)
                .inviterId(currentUserId)
                .inviteeEmail(inviteeEmail)
                .role(inviteeBoardMemberRole)
                .status(BoardInvitationStatus.PENDING)
                .uuid(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusDays(BOARD_INVITATION_EXPIRE_DAY))
                .build());

        applicationEventPublisher.publishEvent(new InvitationEventReqDto(boardId, inviteeEmail));
    }

    @Transactional
    public void acceptInvitation(String uuid) {

        BoardInvitation invitation = boardInvitationRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("찾을 수 없는 초대 입니다."));

        if (invitation.getStatus() == BoardInvitationStatus.ACCEPTED) {

            throw new DuplicateResourceException("이미 수락한 초대 입니다.");
        }

        if (invitation.getExpiredAt().isBefore(LocalDateTime.now())) {

            throw new ExpiredResourceException("만료된 초대 입니다.");
        }

        User user = userService.findByEmail(invitation.getInviteeEmail())
                .orElseThrow(() -> new ResourceNotFoundException("회원가입이 필요합니다."));

        invitation.accept();

        createBoardMember(CreateBoardMemberReqDto.builder()
                .boardId(invitation.getBoardId())
                .userId(user.getId())
                .boardMemberRole(invitation.getRole())
                .build());
    }

    public Optional<Board> findById(Long boardId) {

        return boardRepository.findById(boardId);
    }

    public BoardMemberRole getBoardMemberRole(Long userId, Long boardId) {

        BoardMember member = boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElseThrow(() ->
                new ResourceNotFoundException("멤버를 찾을 수 없습니다."));

        return member.getRole();
    }

    public Boolean existByUserIdAndBoardId(Long boardId, Long userId) {

        return boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    public Long getLastTicketSequence(Long boardId) {

        return boardRepository.getLastTicketSequenceByBoardId(boardId);
    }

    @Transactional
    public void updateBoard(Long boardId, UpdateBoardReqDto updateBoardReqDto) {

        Long userId = userService.getCurrentUser().getId();

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다."));

        BoardMember member = boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElseThrow(() ->
                new AccessDeniedException("보드 접근 권한이 없습니다."));

        if (member.getRole() == BoardMemberRole.MEMBER) {

            throw new AccessDeniedException("보드 수정 권한이 없습니다.");
        }

        board.updateName(updateBoardReqDto.getName());
    }

    @Transactional
    public void deleteBoard(Long boardId) {

        Long userId = userService.getCurrentUser().getId();

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다."));

        BoardMember member = boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElseThrow(() ->
                new AccessDeniedException("보드 접근 권한이 없습니다."));

        if (member.getRole() != BoardMemberRole.OWNER) {

            throw new AccessDeniedException("보드 삭제 권한이 없습니다.");
        }

        deleteInvitationAllByBoardId(boardId);
        ticketRepository.deleteAllByBoardId(boardId);
        boardMemberRepository.deleteAllByBoardId(boardId);

        boardRepository.delete(board);
    }

    private void deleteInvitationAllByBoardId(Long boardId) {

        boardInvitationRepository.deleteAllByBoardId(boardId);
    }
}
