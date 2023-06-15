package ru.panic.template.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.panic.security.jwt.JwtUtil;
import ru.panic.template.dto.ChangeDataRequestDto;
import ru.panic.template.dto.ChangeUserDataRequestDto;
import ru.panic.template.dto.SignInRequestDto;
import ru.panic.template.dto.SignInResponseDto;
import ru.panic.template.entity.User;
import ru.panic.template.entity.UserActivity;
import ru.panic.template.enums.Rank;
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

        User.Data data = new User.Data();
        data.setBtcBalance(0D);
        data.setEthBalance(0D);
        data.setXrpBalance(0D);
        data.setTrxBalance(0D);
        data.setLtcBalance(0D);
        data.setMaticBalance(0D);
        data.setTonBalance(0D);
        data.setTetherERC20Balance(0D);
        data.setLevel(new User.Data.Level(Rank.BRONZE, 0D));
        data.setIpAddress(signInRequest.getData().getIpAddress());
        data.setIsMultiAccount(userRepository.existsByDataIpAddress(signInRequest.getData().getIpAddress()));
        data.setIsAccountNonLocked(false);

        user1.setData(data);
        user1.setUserData(null);
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

    @Override
    public User changeUserData(String jwtToken, ChangeUserDataRequestDto request) {
        if(!jwtUtil.isJwtValid(jwtToken) || jwtUtil.isTokenExpired(jwtToken)){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }
        User user = userRepository.findUserByUsername(jwtUtil.extractUsername(jwtToken));
        user.getUserData().setFirstname(request.getFirstname());
        user.getUserData().setLastname(request.getLastname());
        user.getUserData().setBirthday(request.getBirthday());
        user.getUserData().setGender(request.getGender());
        user.getUserData().setAddress(request.getAddress());

        userRepository.save(user);
        return user;
    }

    @Override
    public User changeData(String jwtToken, ChangeDataRequestDto request) {
        if(!jwtUtil.isJwtValid(jwtToken) || jwtUtil.isTokenExpired(jwtToken)){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        User user = userRepository.findUserByUsername(jwtUtil.extractUsername(jwtToken));

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Неверный старый пароль");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        return user;
    }
}
