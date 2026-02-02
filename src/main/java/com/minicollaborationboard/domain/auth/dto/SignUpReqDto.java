package com.minicollaborationboard.domain.auth.dto;

import lombok.Data;

@Data
public class SignUpReqDto {

    private String email;
    private String password;
    private String name;
}
