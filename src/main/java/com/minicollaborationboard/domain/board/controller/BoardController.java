package com.minicollaborationboard.domain.board.controller;

import com.minicollaborationboard.domain.board.dto.BoardResDto;
import com.minicollaborationboard.domain.board.dto.CreateBoardReqDto;
import com.minicollaborationboard.domain.board.dto.CreateInviReqDto;
import com.minicollaborationboard.domain.board.service.BoardService;
import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestBody CreateBoardReqDto createBoardReqDto) {

        boardService.createBoard(createBoardReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public Page<BoardResDto> getBoards(@RequestParam(required = false) Long boardId,
                                       @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return boardService.getBoards(boardId, pageable);
    }

    @PostMapping("/invitation")
    public ResponseEntity<Void> createInvitation(@RequestBody CreateInviReqDto createInviReqDto) throws AccessDeniedException, MessagingException {

        boardService.createInvitation(createInviReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
