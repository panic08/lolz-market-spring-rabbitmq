package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.entity.UserActivity;

@Data
public class SignInRequestDto {
    private String username;
    private String password;
    private UserActivity.UserActivityData data;
}
