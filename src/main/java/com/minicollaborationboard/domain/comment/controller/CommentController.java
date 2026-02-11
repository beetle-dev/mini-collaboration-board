package com.minicollaborationboard.domain.comment.controller;

import com.minicollaborationboard.domain.comment.dto.CreateCommentReqDto;
import com.minicollaborationboard.domain.comment.dto.UpdateCommentReqDto;
import com.minicollaborationboard.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment", description = "댓글 관리")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a comment", description = "권한 검사 후 댓글이 생성됩니다.")
    public ResponseEntity<Void> createComment(@Valid @RequestBody CreateCommentReqDto createCommentReqDto) {

        commentService.createComment(createCommentReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "update a comment", description = "권한 검사 후 댓글이 수정됩니다.")
    public ResponseEntity<Void> updateComment(@PathVariable("commentId") Long commentId,
                                              @Valid @RequestBody UpdateCommentReqDto updateCommentReqDto) {

        commentService.updateComment(commentId, updateCommentReqDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "update a comment", description = "권한 검사 후 댓글이 삭제됩니다.")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }
}
