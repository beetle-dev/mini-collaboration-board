package com.minicollaborationboard.domain.board;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity
public class BoardMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BoardMemberRole role;
    private Long boardId;
    private Long userId;
    private Timestamp joinedAt;

}
