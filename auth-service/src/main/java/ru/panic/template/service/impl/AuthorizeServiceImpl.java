package ru.panic.template.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.panic.security.jwt.JwtUtil;
import ru.panic.template.dto.SignInRequestDto;
import ru.panic.template.dto.SignInResponseDto;
import ru.panic.template.entity.User;
import ru.panic.template.entity.UserActivity;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.UserActivityRepository;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.service.AuthorizeService;

@Service
@Slf4j
public class AuthorizeServiceImpl implements AuthorizeService {
    public AuthorizeServiceImpl(UserRepository userRepository, UserActivityRepository userActivityRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userActivityRepository = userActivityRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequest) {
        log.info("Starting method signIn");
        User user = userRepository.findUserByUsername(signInRequest.getUsername());
        if(user == null || !passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())){
            log.warn("User not founded on method signIn");
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setStatus(200);
        signInResponseDto.setUsername(signInRequest.getUsername());
        signInResponseDto.setJwtToken(jwtUtil.generateToken(user));
        signInResponseDto.setTimestamp(System.currentTimeMillis());

        UserActivity userActivity = new UserActivity();
        userActivity.setUsername(signInRequest.getUsername());
        userActivity.setData(
                new UserActivity.UserActivityData(
                        signInRequest.getData().getIpAddress(),
                        signInRequest.getData().getGeolocation(),
                        signInRequest.getData().getDeviceInfo(),
                        signInRequest.getData().getBrowserInfo(),
                        System.currentTimeMillis()
                        ));
        userActivityRepository.save(userActivity);
        return signInResponseDto;
    }

    @Override
    public SignInResponseDto signUp(SignInRequestDto signInRequest) {
        log.info("Starting method signUp");
        User user = userRepository.findUserByUsername(signInRequest.getUsername());
        if (user != null){
            log.warn("User with username {} was founded on method signUp", user.getUsername());
            throw new InvalidCredentialsException("Данный пользователь уже существует");
        }
        User user1 = new User();
        user1.setUsername(signInRequest.getUsername());
        user1.setPassword(passwordEncoder.encode(signInRequest.getPassword()));
        user1.setTimestamp(System.currentTimeMillis());

        userRepository.save(user1);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setStatus(200);
        signInResponseDto.setUsername(signInRequest.getUsername());
        signInResponseDto.setJwtToken(jwtUtil.generateToken(user1));
        signInResponseDto.setTimestamp(System.currentTimeMillis());

        UserActivity userActivity = new UserActivity();
        userActivity.setUsername(signInRequest.getUsername());
        userActivity.setData(
                new UserActivity.UserActivityData(
                        signInRequest.getData().getIpAddress(),
                        signInRequest.getData().getGeolocation(),
                        signInRequest.getData().getDeviceInfo(),
                        signInRequest.getData().getBrowserInfo(),
                        System.currentTimeMillis()
                ));
        userActivityRepository.save(userActivity);
        return signInResponseDto;
    }

    @Override
    public User getInfoByJwt(String jwtToken) {
        if(!jwtUtil.isJwtValid(jwtToken) || jwtUtil.isTokenExpired(jwtToken)){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }
        return userRepository.findUserByUsername(jwtUtil.extractUsername(jwtToken));
    }
}
