package com.minicollaborationboard.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtClaims {

    private String uuid;
    private String email;
    private String role;
}
