package ru.panic.template.dto;

import lombok.Data;
@Data
public class ChangeDataRequestDto {
    private String oldPassword;
    private String newPassword;
}
