package com.minicollaborationboard.domain.board.service;

import com.minicollaborationboard.domain.auth.service.AuthService;
import com.minicollaborationboard.domain.board.dto.BoardResDto;
import com.minicollaborationboard.domain.board.dto.CreateBoardReqDto;
import com.minicollaborationboard.domain.board.entity.Board;
import com.minicollaborationboard.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final AuthService authService;

    public void createBoard(CreateBoardReqDto createBoardReqDto) {

        Long userId = authService.getUserId();
        String boardName = createBoardReqDto.getName();

        if (boardRepository.existsByName(boardName)) {

            throw new IllegalArgumentException("이미 존재하는 보드명 입니다.");
        }

        Board board = Board.builder()
                .name(boardName)
                .ownerId(userId)
                .build();

        boardRepository.save(board);
    }

    public Page<BoardResDto> getBoards(Long boardId, Pageable pageable) {

        Page<Board> boards = boardRepository.findBoards(boardId, pageable);

        return boards.map(this::toBoardResDto);
    }

    private BoardResDto toBoardResDto(Board board) {

        return BoardResDto.builder()
                .id(board.getId())
                .name(board.getName())
                .ownerId(board.getOwnerId())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
