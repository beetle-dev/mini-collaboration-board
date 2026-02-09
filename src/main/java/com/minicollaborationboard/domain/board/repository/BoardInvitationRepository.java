package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.BoardInvitation;
import com.minicollaborationboard.domain.board.entity.BoardInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardInvitationRepository extends JpaRepository<BoardInvitation, Long> {
    boolean existsByBoardIdAndInviteeEmailAndStatus(Long boardId, String inviteeEmail, BoardInvitationStatus boardInvitationStatus);

    Optional<BoardInvitation> findByUuid(String uuid);

    void deleteAllByBoardId(Long boardId);
}
