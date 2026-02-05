package com.minicollaborationboard.domain.board.dto;

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
public class CreateBoardReqDto {

    @NotBlank
    private String name;

    @NotBlank @Pattern(regexp = "^[A-Z]+$")
    private String code;
}
