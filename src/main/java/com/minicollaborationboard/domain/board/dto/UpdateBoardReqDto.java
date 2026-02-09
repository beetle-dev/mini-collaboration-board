package com.minicollaborationboard.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "보드 수정 요청 DTO")
public class UpdateBoardReqDto {

    @NotBlank
    private String name;
}
