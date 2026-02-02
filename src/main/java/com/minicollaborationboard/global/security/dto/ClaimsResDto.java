package com.minicollaborationboard.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ClaimsResDto {

    private String username;
    private String role;
}
