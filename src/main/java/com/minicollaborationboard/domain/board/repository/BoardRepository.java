package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    boolean existsByName(String name);

    Page<Board> findById(@Param("boardId") Long boardId, Pageable pageable);

    Page<Board> findByIdIn(List<Long> boardIds, Pageable pageable);

    Page<Board> findByIdInAndNameContainingIgnoreCase(List<Long> boardIds, String name, Pageable pageable);

    @Query("select b.lastTicketSequence from Board b where b.id = :boardId")
    Long getLastTicketSequenceByBoardId(@Param("boardId") Long boardId);
}
