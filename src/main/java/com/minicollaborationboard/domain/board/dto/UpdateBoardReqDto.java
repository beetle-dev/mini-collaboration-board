package com.minicollaborationboard.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UpdateBoardReqDto {

    @NotBlank
    private String name;
}
