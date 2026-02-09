package com.minicollaborationboard.domain.board.entity;

import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private Long ownerId;

    @Builder.Default
    private Long lastTicketSequence = 0L;

    public void updateName(String name) {

        this.name = name;
    }
}
