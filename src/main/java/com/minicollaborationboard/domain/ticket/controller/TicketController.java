package com.minicollaborationboard.domain.ticket.controller;

import com.minicollaborationboard.domain.ticket.dto.*;
import com.minicollaborationboard.domain.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Void> createTicket(@Valid @RequestBody CreateTicketReqDto createTicketReqDto) {

        ticketService.createTicket(createTicketReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<TicketResDto> getTickets(@RequestParam(required = false) GetTicketReqDto getTicketReqDto) {

        return ticketService.getTickets(getTicketReqDto);
    }

    @PutMapping("/info")
    public ResponseEntity<Void> updateTicketInfo(@Valid @RequestBody UpdateTicketReqDto.TicketInfoDto updateTicketReqDto){

        ticketService.updateTicketInfo(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/assignee")
    public ResponseEntity<Void> updateTicketAssignee(@Valid @RequestBody UpdateTicketReqDto.TicketAssigneeDto updateTicketReqDto) {

        ticketService.updateTicketAssignee(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateTicketStatus(@Valid @RequestBody UpdateTicketReqDto.TicketStatusDto updateTicketReqDto) {

        ticketService.updateTicketStatus(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTicket(@Valid @RequestBody DeleteTicketReqDto deleteTicketReqDto){

        ticketService.deleteTicket(deleteTicketReqDto);

        return ResponseEntity.ok().build();
    }
}
