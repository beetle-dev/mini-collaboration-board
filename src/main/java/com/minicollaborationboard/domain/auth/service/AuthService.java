package com.minicollaborationboard.domain.auth.service;

import com.minicollaborationboard.domain.board.entity.BoardMember;
import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import com.minicollaborationboard.domain.board.repository.BoardMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BoardMemberRepository boardMemberRepository;

    /**
     * Validate Access Board Permission
     */
    public BoardMember validateAccessPermission(Long boardId) {

        Long userId = userService.getCurrentUser().getId();

        return boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElseThrow(() ->
                new AccessDeniedException("접근 권한이 없습니다."));
    }

    /**
     * Validate Create Invitation Permission
     */
    public void validateCreateInvitationPermission (Long boardId, BoardMemberRole inviteeBoardMemberRole) {

        BoardMemberRole currentBoardMemberRole = validateAccessPermission(boardId).getRole();

        if (currentBoardMemberRole == BoardMemberRole.MEMBER ||
                (currentBoardMemberRole == BoardMemberRole.ADMIN && inviteeBoardMemberRole == BoardMemberRole.ADMIN)) {

            throw new AccessDeniedException("초대 권한이 없습니다.");
        }
    }

    /**
     * Validate Update Board/Ticket Permission
     */
    public void validateUpdatePermission(Long boardId) {

        BoardMember member = validateAccessPermission(boardId);

        if (member.getRole() == BoardMemberRole.MEMBER) {

            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
    }

    /**
     * Validate Delete Board/Ticket Permission
     */
    public void validateDeletePermission(Long boardId) {

        BoardMember member = validateAccessPermission(boardId);

        if (member.getRole() != BoardMemberRole.ADMIN) {

            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    }

    /**
     * Validate Update Comment Permission
     */
    public void validateUpdateCommentPermission(Long authorId) {

        Long userId = userService.getCurrentUser().getId();

        if (!Objects.equals(userId, authorId)) {

            throw new AccessDeniedException("수정/삭제 권한이 없습니다.");
        }
    }
}
