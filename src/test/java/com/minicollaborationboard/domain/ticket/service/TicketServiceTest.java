package com.minicollaborationboard.domain.ticket.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.domain.ticket.dto.CreateTicketReqDto;
import com.minicollaborationboard.domain.ticket.entity.Ticket;
import com.minicollaborationboard.domain.ticket.repository.TicketRepository;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    AuthService authService;

    @Mock
    BoardService boardService;

    @Mock
    TicketRepository ticketRepository;

    Long userId;
    User user;
    Long boardId;
    Board board;

    @BeforeEach
    void initialize() {

        userId = 9L;

        user = User.builder()
                .id(userId)
                .build();

        boardId = 99L;

        board = Board.builder()
                .id(boardId)
                .code("TEST")
                .build();
    }

    @Test
    void 정상_createTicket() {

        // given
        CreateTicketReqDto createTicketReqDto = CreateTicketReqDto.builder()
                .boardId(boardId)
                .build();

        given(authService.getCurrentUser()).willReturn(user);
        given(boardService.findById(boardId)).willReturn(Optional.ofNullable(board));
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
    void 보드없음_createTicket() {

        // given
        CreateTicketReqDto createTicketReqDto = CreateTicketReqDto.builder()
                .boardId(boardId)
                .build();

        given(authService.getCurrentUser()).willReturn(user);
        given(boardService.findById(anyLong())).willReturn(null);

        // when
        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.createTicket(createTicketReqDto));
    }
}