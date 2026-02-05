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

    @PutMapping
    public ResponseEntity<Void> updateTicket(@Valid @RequestBody UpdateTicketReqDto updateTicketReqDto){

        ticketService.updateTicket(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTicket(@Valid @RequestBody DeleteTicketReqDto deleteTicketReqDto){

        ticketService.deleteTicket(deleteTicketReqDto);

        return ResponseEntity.ok().build();
    }
}
