package ru.panic.template.service;

import ru.panic.template.dto.SignInRequestDto;
import ru.panic.template.dto.SignInResponseDto;
import ru.panic.template.entity.User;

public interface AuthorizeService {
    SignInResponseDto signIn(SignInRequestDto signInRequest);
    SignInResponseDto signUp(SignInRequestDto signInRequest);
    User getInfoByJwt(String jwtToken);
}
