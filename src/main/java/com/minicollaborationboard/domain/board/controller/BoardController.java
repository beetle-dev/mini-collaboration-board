package com.minicollaborationboard.domain.board.controller;

import com.minicollaborationboard.domain.board.dto.*;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.exception.DuplicateResourceException;
import com.minicollaborationboard.exception.ExpiredResourceException;
import com.minicollaborationboard.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "보드 관리")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "Create a board", description = "보드명은 중복이 불가합니다. 하나의 트랜잭션에서 보드 생성 후 OWNER 권한의 보드 멤버도 생성됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "보드 생성 성공")})
    public ResponseEntity<Void> createBoard(@Valid @RequestBody CreateBoardReqDto createBoardReqDto) {

        boardService.createBoard(createBoardReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get boards", description = "boardId/boardName으로 조회할 수 있습니다. 둘 다 없으면 현재 유저가 속한 보드만 조회합니다.")
    public Page<BoardResDto> getBoards(@RequestParam(required = false) Long boardId,
                                       @RequestParam(required = false) String boardName,
                                       @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return boardService.getBoards(boardId, boardName, pageable);
    }

    @PostMapping("/{boardId}/invitation")
    @Operation(summary = "Create a invitation API", description = "보드 참가 여부, 중복 초대, 초대 권한을 검증하여 초대를 생성하고 초대 이메일을 전송합니다. 보드 멤버 권한이 MEMBER 이거나 ADMIN 유저가 ADMIN 유저를 초대할 경우 인가되지 않습니다.")
    public ResponseEntity<Void> createInvitation(@PathVariable Long boardId,
                                                 @Valid @RequestBody CreateInvitationReqDto createInvitationReqDto) {

        boardService.createInvitation(boardId, createInvitationReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/invitation/{uuid}/accept")
    @Operation(summary = "Accept a invitation API", description = "초대 유효성 검사 진행 후 초대 상태를 변경하고 보드 멤버를 생성합니다.")
    public ResponseEntity<Void> acceptInvitation(@PathVariable String uuid) {

        try {
            boardService.acceptInvitation(uuid);

            URI redirectUri = URI.create("/invitation/accepted.html");

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();
        } catch (DuplicateResourceException | ExpiredResourceException | ResourceNotFoundException e) {

            URI redirectUri = URI.create("/invitation/accept-error.html");

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();
        }
    }

    @PatchMapping("/{boardId}")
    @Operation(summary = "Update a board", description = "보드 유효성 검사, 수정 권한 확인 후 보드명을 수정합니다.")
    public ResponseEntity<Void> updateBoard(@PathVariable Long boardId,
                                            @Valid @RequestBody UpdateBoardReqDto updateBoardReqDto) {

        boardService.updateBoard(boardId, updateBoardReqDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "Delete a board", description = "보드 유효성 검사, 삭제 권한 확인 후 보드뿐만 아니라 참조하고 있는 초대, 티켓, 보드 멤버를 모두 삭제합니다.")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{boardId}/boardmember")
    @Operation(summary = "Update a board member's role", description = "Board의 OWNER만 BoardMember의 Role을 변경할 수 있습니다.")
    public ResponseEntity<Void> updateBoardMemberRole(
            @PathVariable("boardId") Long boardId,
            @RequestBody UpdateBoardMemberRoleReqDto updateBoardMemberRoleReqDto) {

        boardService.updateBoardMemberRole(boardId, updateBoardMemberRoleReqDto);

        return ResponseEntity.noContent().build();
    }
}
