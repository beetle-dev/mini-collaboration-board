package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    Optional<BoardMember> findByUserIdAndBoardId(Long currentUserId, Long boardId);

    void deleteAllByBoardId(Long boardId);
}
