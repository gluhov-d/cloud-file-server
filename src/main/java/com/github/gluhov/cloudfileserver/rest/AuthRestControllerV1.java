package com.github.gluhov.cloudfileserver.rest;

import com.github.gluhov.cloudfileserver.dto.AuthRequestDto;
import com.github.gluhov.cloudfileserver.dto.AuthResponseDto;
import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.security.SecurityService;
import com.github.gluhov.cloudfileserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AuthRestControllerV1.REST_URL)
public class AuthRestControllerV1 {
    public static final String REST_URL = "/api/v1/auth";
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<?> register(@RequestBody UserDto userDto) {
        return userService.registerUser(userMapper.map(userDto))
                .map(user -> ResponseEntity.ok().body(userMapper.map(user)));
    }

    @PostMapping("/login")
    public Mono<?> login(@RequestBody AuthRequestDto authRequestDto) {
        return securityService.authenticate(authRequestDto.getUsername(), authRequestDto.getPassword())
                .map(tokenDetails -> ResponseEntity.ok().body(AuthResponseDto.builder()
                        .userId(tokenDetails.getUserId())
                        .token(tokenDetails.getToken())
                        .issuedAt(tokenDetails.getIssuedAt())
                        .expiresAt(tokenDetails.getExpiresAt())
                        .build()));
    }
}