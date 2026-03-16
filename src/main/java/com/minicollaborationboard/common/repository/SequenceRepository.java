package com.minicollaborationboard.common.repository;

import com.minicollaborationboard.common.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SequenceRepository extends JpaRepository<Sequence, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update Sequence s set s.sequence = LAST_INSERT_ID(s.sequence + 1) where s.code = :code", nativeQuery = true)
    int increamentSequence(@Param("code") String code);

    @Query(value = "select LAST_INSERT_ID()", nativeQuery = true)
    Long findLastInsertId();

    boolean existsByCode(String code);
}
