package com.minicollaborationboard.domain.ticket.controller;

import com.minicollaborationboard.domain.ticket.dto.*;
import com.minicollaborationboard.domain.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "티켓 관리")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a ticket", description = "티켓을 생성할 보드 유효성 검사 후 시퀀스를 증가시킨 후 티켓을 생성합니다.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "티켓 생성 성공")})
    public ResponseEntity<Void> createTicket(@Valid @RequestBody CreateTicketReqDto createTicketReqDto) {

        ticketService.createTicket(createTicketReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get tickets", description = "검색 조건에 따라 전체 또는 특정 보드만을 반환합니다.")
    public List<TicketResDto> getTickets(TicketSearchDto ticketSearchDto) {

        return ticketService.getTickets(ticketSearchDto);
    }

    @PatchMapping("/info")
    @Operation(summary = "Update a ticket's information", description = "수정 권한 확인 후 티켓 일부를 수정합니다.")
    public ResponseEntity<Void> updateTicketInfo(@Valid @RequestBody UpdateTicketReqDto.Info updateTicketReqDto){

        ticketService.updateTicketInfo(updateTicketReqDto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/assignee")
    @Operation(summary = "Update ticket's assignee", description = "수정 권한 확인 후 담장자를 수정합니다.")
    public ResponseEntity<Void> updateTicketAssignee(@Valid @RequestBody UpdateTicketReqDto.Assignee updateTicketReqDto) {

        ticketService.updateTicketAssignee(updateTicketReqDto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status")
    @Operation(summary = "Update ticket's status", description = "수정 권한 확인 후 상태를 수정합니다.")
    public ResponseEntity<Void> updateTicketStatus(@Valid @RequestBody UpdateTicketReqDto.Status updateTicketReqDto) {

        ticketService.updateTicketStatus(updateTicketReqDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Delete a ticket", description = "삭제 권한 확인 후 티켓을 삭제합니다. 티켓은 OWNER 또는 ADMIN만 삭제가 가능합니다.")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId){

        ticketService.deleteTicket(ticketId);

        return ResponseEntity.noContent().build();
    }
}
