package com.minicollaborationboard.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 수정 요청 DTO")
public class UpdateCommentReqDto {

    @NotBlank
    private String content;
}
