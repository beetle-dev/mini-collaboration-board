package com.minicollaborationboard.domain.auth.dto;

import lombok.Data;

@Data
public class SingUpReqDto {

    private String email;
    private String password;
    private String name;
}
