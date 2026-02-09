package com.minicollaborationboard.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "보드 생성 요청 DTO")
public class CreateBoardReqDto {

    @NotBlank
    private String name;

    @NotBlank @Pattern(regexp = "^[A-Z]+$")
    private String code;
}
