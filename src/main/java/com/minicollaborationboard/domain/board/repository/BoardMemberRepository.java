package com.minicollaborationboard.domain.board.repository;

import com.minicollaborationboard.domain.board.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    Optional<BoardMember> findByUserIdAndBoardId(Long currentUserId, Long boardId);

    @Query("select bm.boardId from BoardMember bm where bm.userId = :userId")
    List<Long> findBoardIdsByUserId(@Param("userId") Long userId);

    void deleteAllByBoardId(Long boardId);
}
