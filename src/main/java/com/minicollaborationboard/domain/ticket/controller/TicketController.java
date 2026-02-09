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
    public List<TicketResDto> getTickets(TicketSearchDto ticketSearchDto) {

        return ticketService.getTickets(ticketSearchDto);
    }

    @PatchMapping("/info")
    public ResponseEntity<Void> updateTicketInfo(@Valid @RequestBody UpdateTicketReqDto.Info updateTicketReqDto){

        ticketService.updateTicketInfo(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/assignee")
    public ResponseEntity<Void> updateTicketAssignee(@Valid @RequestBody UpdateTicketReqDto.Assignee updateTicketReqDto) {

        ticketService.updateTicketAssignee(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateTicketStatus(@Valid @RequestBody UpdateTicketReqDto.Status updateTicketReqDto) {

        ticketService.updateTicketStatus(updateTicketReqDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId){

        ticketService.deleteTicket(ticketId);

        return ResponseEntity.ok().build();
    }
}
