package com.minicollaborationboard.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 생성 요청 DTO")
public class CreateCommentReqDto {

    @NotBlank
    private String content;

    @NotNull
    private Long ticketId;
}
