package com.minicollaborationboard.domain.board.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.board.dto.CreateInvitationReqDto;
import com.minicollaborationboard.domain.board.entity.*;
import com.minicollaborationboard.domain.board.repository.BoardInvitationRepository;
import com.minicollaborationboard.domain.board.repository.BoardMemberRepository;
import com.minicollaborationboard.domain.board.repository.BoardRepository;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.common.EmailService;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import com.minicollaborationboard.global.exception.ExpiredResourceException;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @Mock
    AuthService authService;

    @Mock
    BoardMemberRepository boardMemberRepository;

    @Mock
    BoardInvitationRepository boardInvitationRepository;

    @Mock
    EmailService emailService;

    Long boardId;
    Long currentUserId;
    String inviteeEmail;
    Long currentBoardMemberId;
    Board board;
    User currentUser;
    BoardInvitation boardInvitation;

    @BeforeEach
    void givenSetting() {

        boardId = 97L;

        currentUserId = 99L;

        currentBoardMemberId = 98L;

        inviteeEmail = "test@test,com";

        board = Board.builder()
                .id(boardId)
                .build();

        currentUser = User.builder()
                .id(currentUserId)
                .build();

        boardInvitation = BoardInvitation.builder()
                .inviterId(currentUserId)
                .inviteeEmail(inviteeEmail)
                .boardId(boardId)
                .status(BoardInvitationStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .role(BoardMemberRole.MEMBER)
                .build();
    }

    @Test
    void 정상_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.OWNER)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.MEMBER)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.ofNullable(currentBoardMember));
        given(boardInvitationRepository.existsByBoardIdAndInviteeEmailAndStatus(anyLong(), anyString(), any()))
                .willReturn(false);
        given(boardInvitationRepository.save(any(BoardInvitation.class))).willReturn(boardInvitation);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        // when
        boardService.createInvitation(boardId, createInvitationReqDto);

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository).save(captor.capture());

        BoardInvitation boardInvitation1 = captor.getValue();

        assertThat(boardInvitation1.getBoardId()).isEqualTo(boardId);
        assertThat(boardInvitation1.getInviteeEmail()).isEqualTo(inviteeEmail);
        assertThat(boardInvitation1.getStatus()).isEqualTo(BoardInvitationStatus.PENDING);
    }

    @Test
    void 초대자가속하지않은경우_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.OWNER)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.MEMBER)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.empty());

        // when
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            boardService.createInvitation(boardId, createInvitationReqDto);
        });

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository, never()).save(captor.capture());
    }

    @Test
    void 초대자가멤버권한_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.MEMBER)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.MEMBER)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.ofNullable(currentBoardMember));

        // when
        assertThrows(AccessDeniedException.class, () -> {
            boardService.createInvitation(boardId, createInvitationReqDto);
        });

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository, never()).save(captor.capture());
    }

    @Test
    void 초대자가어드민권한_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.ADMIN)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.MEMBER)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.ofNullable(currentBoardMember));
        given(boardInvitationRepository.existsByBoardIdAndInviteeEmailAndStatus(anyLong(), anyString(), any()))
                .willReturn(false);
        given(boardInvitationRepository.save(any(BoardInvitation.class))).willReturn(boardInvitation);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        // when
        boardService.createInvitation(boardId, createInvitationReqDto);

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository).save(captor.capture());

        BoardInvitation boardInvitation1 = captor.getValue();

        assertThat(boardInvitation1.getBoardId()).isEqualTo(boardId);
        assertThat(boardInvitation1.getInviteeEmail()).isEqualTo(inviteeEmail);
        assertThat(boardInvitation1.getStatus()).isEqualTo(BoardInvitationStatus.PENDING);
    }

    @Test
    void 초대받은사람도어드민권한_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.ADMIN)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.ADMIN)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.ofNullable(currentBoardMember));

        // when
        assertThrows(AccessDeniedException.class, () -> {
            boardService.createInvitation(boardId, createInvitationReqDto);
        });

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository, never()).save(captor.capture());
    }

    @Test
    void 이미초대된경우1_createInvitation() throws MessagingException {

        // given
        BoardMember currentBoardMember = BoardMember.builder()
                .id(currentBoardMemberId)
                .role(BoardMemberRole.OWNER)
                .build();

        CreateInvitationReqDto createInvitationReqDto = CreateInvitationReqDto.builder()
                .inviteeEmail(inviteeEmail)
                .role(BoardMemberRole.MEMBER)
                .build();

        given(authService.getCurrentUser()).willReturn(currentUser);
        given(boardMemberRepository.findByUserIdAndBoardId(anyLong(), anyLong())).willReturn(Optional.ofNullable(currentBoardMember));
        given(boardInvitationRepository.existsByBoardIdAndInviteeEmailAndStatus(anyLong(), anyString(), any()))
                .willReturn( true);

        // when
        assertThrows(DuplicateResourceException.class, () -> {
            boardService.createInvitation(boardId, createInvitationReqDto);
        });

        // then
        ArgumentCaptor<BoardInvitation> captor = ArgumentCaptor.forClass(BoardInvitation.class);
        verify(boardInvitationRepository, never()).save(captor.capture());
    }

    @Test
    void 정상_acceptInvitation() {

        // given
        given(boardInvitationRepository.findByUuid(anyString())).willReturn(Optional.ofNullable(boardInvitation));
        given(authService.findByEmail(anyString())).willReturn(Optional.ofNullable(currentUser));

        // when
        boardService.acceptInvitation(UUID.randomUUID().toString());

        // then
        ArgumentCaptor<BoardMember> captor = ArgumentCaptor.forClass(BoardMember.class);

        verify(boardMemberRepository).save(captor.capture());

        BoardMember boardMember = captor.getValue();

        assertThat(boardMember.getUserId()).isEqualTo(currentUserId);
        assertThat(boardMember.getBoardId()).isEqualTo(boardId);
        assertThat(boardMember.getRole()).isEqualTo(BoardMemberRole.MEMBER);
    }

    @Test
    void uuid이상_acceptInvitation() {

        // given
        given(boardInvitationRepository.findByUuid(anyString())).willReturn(Optional.empty());

        // when
        assertThrows(ResourceNotFoundException.class, () -> boardService.acceptInvitation(UUID.randomUUID().toString()));

        // then
        ArgumentCaptor<BoardMember> captor = ArgumentCaptor.forClass(BoardMember.class);

        verify(boardMemberRepository, never()).save(captor.capture());
    }

    @Test
    void 수락한초대_acceptInvitation() {

        // given
        given(boardInvitationRepository.findByUuid(anyString())).willReturn(Optional.ofNullable(boardInvitation));

        // when
        assertThrows(DuplicateResourceException.class, () -> boardService.acceptInvitation(UUID.randomUUID().toString()));

        // then
        ArgumentCaptor<BoardMember> captor = ArgumentCaptor.forClass(BoardMember.class);

        verify(boardMemberRepository, never()).save(captor.capture());
    }

    @Test
    void 만료된초대_acceptInvitation() {

        // given
        given(boardInvitationRepository.findByUuid(anyString())).willReturn(Optional.ofNullable(boardInvitation));

        // when
        assertThrows(ExpiredResourceException.class, () -> boardService.acceptInvitation(UUID.randomUUID().toString()));

        // then
        ArgumentCaptor<BoardMember> captor = ArgumentCaptor.forClass(BoardMember.class);

        verify(boardMemberRepository, never()).save(captor.capture());
    }

    @Test
    void 비회원초대_acceptInvitation() {

        // given
        given(boardInvitationRepository.findByUuid(anyString())).willReturn(Optional.ofNullable(boardInvitation));
        given(authService.findByEmail(anyString())).willReturn(Optional.empty());

        // when
        assertThrows(ResourceNotFoundException.class, () -> boardService.acceptInvitation(UUID.randomUUID().toString()));

        // then
        ArgumentCaptor<BoardMember> captor = ArgumentCaptor.forClass(BoardMember.class);

        verify(boardMemberRepository, never()).save(captor.capture());
    }

}