package com.minicollaborationboard.domain.ticket.service;

import com.minicollaborationboard.domain.auth.service.UserService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.entity.BoardMemberRole;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.ticket.dto.CreateTicketReqDto;
import com.minicollaborationboard.domain.ticket.dto.UpdateTicketReqDto;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.repository.TicketQueryRepository;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    TicketService ticketService;

    @Mock
    UserService userService;

    @Mock
    BoardService boardService;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    TicketQueryRepository ticketQueryRepository;

    Long userId;
    User user;
    Long boardId;
    Board board;
    Ticket ticket;

    @BeforeEach
    void ingivenSetting() {

        userId = 9L;

        user = User.builder()
                .id(userId)
                .build();

        boardId = 99L;

        board = Board.builder()
                .id(boardId)
                .code("TEST")
                .build();

        ticket = Ticket.builder()
                .boardId(boardId)
                .build();
    }

    @Test
    void createTicket_두번연속호출_시퀀스증가() {

        // given
        CreateTicketReqDto createTicketReqDto = CreateTicketReqDto.builder()
                .boardId(boardId)
                .build();

        given(userService.getCurrentUser()).willReturn(user);
        given(boardService.findById(boardId)).willReturn(Optional.of(board));
        given(boardService.getLastTicketSequence(boardId))
                .willReturn(1L)
                .willReturn(2L);

        // when
        ticketService.createTicket(createTicketReqDto);
        ticketService.createTicket(createTicketReqDto);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository, times(2)).save(captor.capture());

        List<Ticket> tickets = captor.getAllValues();
        assertThat(tickets.get(0).getSequence()).isEqualTo("TEST_1");
        assertThat(tickets.get(1).getSequence()).isEqualTo("TEST_2");
    }

    @Test
    void createTicket_보드없음_예외() {

        // given
        CreateTicketReqDto createTicketReqDto = CreateTicketReqDto.builder()
                .boardId(boardId)
                .build();

        given(userService.getCurrentUser()).willReturn(user);
        given(boardService.findById(anyLong())).willReturn(Optional.empty());

        // when
        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.createTicket(createTicketReqDto));
    }

    @Test
    void updateTicketInfo_티켓없음_예외() {

        // given
        given(ticketRepository.findById(anyLong())).willReturn(Optional.empty());

        // when-then
        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.updateTicketInfo(new UpdateTicketReqDto.Info()));
    }

    @Test
    void updateTicketInfo_수정권한없음_예외() {

        // given
        UpdateTicketReqDto.Info updateTicketReqDto = new UpdateTicketReqDto.Info();
        updateTicketReqDto.setTicketId(999L);

        given(ticketRepository.findById(anyLong())).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(user);
        given(boardService.existByUserIdAndBoardId(boardId, userId)).willReturn(false);

        // when-then
        assertThrows(AccessDeniedException.class, () ->
                ticketService.updateTicketInfo(updateTicketReqDto));
    }

    @Test
    void deleteTicket_삭제권한없음_예외() {

        // given
        given(ticketRepository.findById(anyLong())).willReturn(Optional.ofNullable(ticket));
        given(userService.getCurrentUser()).willReturn(user);
        given(boardService.getBoardMemberRole(userId, boardId)).willReturn(BoardMemberRole.MEMBER);

        // when-then
        assertThrows(AccessDeniedException.class, () ->
                ticketService.deleteTicket(ticket.getId()));
    }
}