package com.minicollaborationboard.domain.board.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.board.dto.*;
import com.minicollaborationboard.domain.board.entity.*;
import com.minicollaborationboard.domain.board.repository.BoardInvitationRepository;
import com.minicollaborationboard.domain.board.repository.BoardMemberRepository;
import com.minicollaborationboard.domain.board.repository.BoardRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.common.EmailService;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import com.minicollaborationboard.global.exception.ExpiredResourceException;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
    private final AuthService authService;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardInvitationRepository boardInvitationRepository;
    private final EmailService emailService;
    private final TicketRepository ticketRepository;

    private static final int BOARD_INVITATION_EXPIRE_DAY = 3;
    private static final String EMAIL_SUBJECT = "Board 초대 메일";

    @Transactional
    public void createBoard(CreateBoardReqDto createBoardReqDto) {

        Long userId = authService.getCurrentUser().getId();
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
    public void createInvitation(Long boardId, CreateInvitationReqDto createInvitationReqDto) throws MessagingException {

        Long currentUserId = authService.getCurrentUser().getId();
        BoardMember currentBoardMember = boardMemberRepository.findByUserIdAndBoardId(currentUserId, boardId)
                .orElseThrow(() -> new ResourceNotFoundException("본인이 속하지 않은 보드에 초대할 수 없습니다."));

        BoardMemberRole currentBoardMemberRole = currentBoardMember.getRole();
        BoardMemberRole inviteeBoardMemberRole = createInvitationReqDto.getRole();

        if (currentBoardMemberRole == BoardMemberRole.MEMBER ||
                currentBoardMemberRole == BoardMemberRole.ADMIN && inviteeBoardMemberRole == BoardMemberRole.ADMIN) {

            throw new AccessDeniedException("초대 권한이 없습니다.");
        }

        String inviteeEmail = createInvitationReqDto.getInviteeEmail();

        if (boardInvitationRepository.existsByBoardIdAndInviteeEmailAndStatus(boardId, inviteeEmail, BoardInvitationStatus.PENDING)) {

            throw new DuplicateResourceException("해당 보드에 이미 초대된 유저입니다.");
        }

        BoardInvitation boardInvitation = boardInvitationRepository.save(BoardInvitation.builder()
                .boardId(boardId)
                .inviterId(currentUserId)
                .inviteeEmail(inviteeEmail)
                .role(inviteeBoardMemberRole)
                .status(BoardInvitationStatus.PENDING)
                .uuid(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusDays(BOARD_INVITATION_EXPIRE_DAY))
                .build());


        sendInvitation(boardInvitation);
    }

    public void sendInvitation(BoardInvitation boardInvitation) throws MessagingException {

        String boardName = boardRepository.findById(boardInvitation.getBoardId()).orElseThrow(() ->
                new ResourceNotFoundException("보드를 찾을 수 없습니다.")).getName();
        String to = boardInvitation.getInviteeEmail();
        String htmlBody = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body style=\"margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, Helvetica, sans-serif;\">\n" +
                "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f6f8; padding:40px 0;\">\n" +
                "      <tr>\n" +
                "        <td align=\"center\">\n" +
                "          <!-- Container -->\n" +
                "          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff; border-radius:8px; overflow:hidden;\">\n" +
                "            \n" +
                "            <!-- Header -->\n" +
                "            <tr>\n" +
                "              <td style=\"padding:24px 32px; background-color:#1f2937;\">\n" +
                "                <h1 style=\"margin:0; color:#ffffff; font-size:20px; font-weight:bold;\">\n" +
                "                  Board Invitation\n" +
                "                </h1>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "\n" +
                "            <!-- Body -->\n" +
                "            <tr>\n" +
                "              <td style=\"padding:32px; color:#111827;\">\n" +
                "                <p style=\"margin:0 0 16px 0; font-size:15px; line-height:1.6;\">\n" +
                "                  안녕하세요,\n" +
                "                </p>\n" +
                "\n" +
                "                <p style=\"margin:0 0 16px 0; font-size:15px; line-height:1.6;\">" +
                "                  <strong> " + boardName + "</strong> 보드에 초대되었습니다.\n" +
                "                </p>\n" +
                "\n" +
                "                <p style=\"margin:0 0 24px 0; font-size:15px; line-height:1.6;\">\n" +
                "                  아래 버튼을 클릭하여 보드에 참여하세요.\n" +
                "                </p>\n" +
                "\n" +
                "                <!-- Button -->\n" +
                "                <table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:24px 0;\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"center\">\n" +
                "                      <a href=\"{{inviteLink}}\"\n" +
                "                         style=\"\n" +
                "                           display:inline-block;\n" +
                "                           padding:12px 24px;\n" +
                "                           background-color:#2563eb;\n" +
                "                           color:#ffffff;\n" +
                "                           text-decoration:none;\n" +
                "                           font-size:14px;\n" +
                "                           font-weight:bold;\n" +
                "                           border-radius:6px;\n" +
                "                         \">\n" +
                "                        보드 초대 수락하기\n" +
                "                      </a>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "\n" +
                "                <p style=\"margin:24px 0 0 0; font-size:13px; color:#6b7280; line-height:1.5;\">\n" +
                "                  버튼이 동작하지 않으면 아래 링크를 복사하여 브라우저에 붙여넣으세요:\n" +
                "                </p>\n" +
                "\n" +
                "                <p style=\"margin:8px 0 0 0; font-size:13px; word-break:break-all;\">\n" +
                "                  <a href=\"{{inviteLink}}\" style=\"color:#2563eb; text-decoration:none;\">\n" +
                "                    {{inviteLink}}\n" +
                "                  </a>\n" +
                "                </p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "\n" +
                "            <!-- Footer -->\n" +
                "            <tr>\n" +
                "              <td style=\"padding:20px 32px; background-color:#f9fafb; font-size:12px; color:#6b7280;\">\n" +
                "                이 초대는 발신 일시부터 3일까지 유효합니다.<br/>\n" +
                "                본 메일은 발신 전용입니다.\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "\n" +
                "          </table>\n" +
                "          <!-- /Container -->\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>\n";

        emailService.sendHtmlMessage(to, EMAIL_SUBJECT, htmlBody);
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

        User user = authService.findByEmail(invitation.getInviteeEmail())
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

    public Boolean exsistByUserIdAndBoardId(Long boardId, Long userId) {

        return boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    public void increaseLastTicketSequence(Long boardId) {

        boardRepository.incrementLastTicketSequence(boardId);
    }

    public Long getLastTicketSequence(Long boardId) {

        return boardRepository.getLastTicketSequenceByBoardId(boardId);
    }

    @Transactional
    public void updateBoard(Long boardId, UpdateBoardReqDto updateBoardReqDto) {

        Long userId = authService.getCurrentUser().getId();

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

        Long userId = authService.getCurrentUser().getId();

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
