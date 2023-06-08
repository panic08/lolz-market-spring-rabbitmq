package ru.panic.template.dto;

import lombok.Data;

@Data
public class SignInResponseDto {
    private Integer status;
    private String username;
    private String jwtToken;
    private Long timestamp;
}
