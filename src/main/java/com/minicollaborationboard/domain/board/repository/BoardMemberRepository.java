package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    boolean existsByBoardIdAndOwnerId(Long boardId, Long userId);

    BoardMember findByOwnerId(Long currentUserId);
}
