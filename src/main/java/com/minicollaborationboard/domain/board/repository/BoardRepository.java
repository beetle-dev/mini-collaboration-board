package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    boolean existsByName(String name);

    @Query("SELECT b from Board b where (:boardId IS NULL OR b.id = :boardId)")
    Page<Board> findBoards(@Param("boardId") Long boardId, Pageable pageable);

    @Modifying
    @Query("UPDATE Board b SET b.lastTicketSequence = b.lastTicketSequence + 1 where b.id = :boardId")
    void incrementLastTicketSequence(@Param("boardId") Long boardId);

    @Query("select b.lastTicketSequence from Board b where b.id = :boardId")
    Long getLastTicketSequenceByBoardId(@Param("boardId") Long boardId);
}
