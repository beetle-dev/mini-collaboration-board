package com.minicollaborationboard.domain.board.entity;

import com.minicollaborationboard.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardInvitation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;
    private Long inviterId;
    private String inviteeEmail;
    private String uuid;
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private BoardMemberRole role;

    @Enumerated(EnumType.STRING)
    private BoardInvitationStatus status;

    public void accept() {
        this.status = BoardInvitationStatus.ACCEPTED;
    }
}
