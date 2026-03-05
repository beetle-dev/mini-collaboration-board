package com.minicollaborationboard.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@Schema(description = "보드 생성 요청 DTO")
public class CreateBoardReqDto {

    @NotEmpty
    private String name;

    @NotEmpty @Pattern(regexp = "^[A-Z]+$")
    private String code;
}
