package com.minicollaborationboard.domain.board.controller;

import com.minicollaborationboard.domain.board.dto.BoardResDto;
import com.minicollaborationboard.domain.board.dto.CreateBoardReqDto;
import com.minicollaborationboard.domain.board.dto.CreateInvitationReqDto;
import com.minicollaborationboard.domain.board.dto.UpdateBoardReqDto;
import com.minicollaborationboard.domain.board.service.BoardService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> createBoard(@Valid @RequestBody CreateBoardReqDto createBoardReqDto) {

        boardService.createBoard(createBoardReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public Page<BoardResDto> getBoards(@RequestParam(required = false) Long boardId,
                                       @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return boardService.getBoards(boardId, pageable);
    }

    @PostMapping("/{boardId}/invitation")
    public ResponseEntity<Void> createInvitation(@PathVariable Long boardId,
                                                 @Valid @RequestBody CreateInvitationReqDto createInvitationReqDto) throws MessagingException {

        boardService.createInvitation(boardId, createInvitationReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/invitation/{uuid}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable String uuid) {

        boardService.acceptInvitation(uuid);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(@PathVariable Long boardId,
                                            @Valid @RequestBody UpdateBoardReqDto updateBoardReqDto) {

        boardService.updateBoard(boardId, updateBoardReqDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseEntity.noContent().build();
    }
}
