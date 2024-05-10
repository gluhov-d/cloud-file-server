package com.github.gluhov.cloudfileserver.service.user;

import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import com.github.gluhov.cloudfileserver.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    void registerUser() {
        when(userRepository.save(any())).thenReturn(Mono.just(user));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userMapper.map(user)).thenReturn(new UserDto(user));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.empty());
        userService.registerUser(user)
                .subscribe(saved -> assertEquals(user, saved));

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(user));
        UserDto userDto = new UserDto(user);
        when(userMapper.map(user)).thenReturn(userDto);
        userService.getUserById(USER_ID)
                .subscribe(foundUser -> assertEquals(userDto, foundUser));

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getNotFoundUserById() {
        when(userRepository.findById(USER_NOT_FOUND_ID)).thenReturn(Mono.empty());
        userService.getUserById(USER_NOT_FOUND_ID)
                .subscribe(Assertions::assertNull);

        verify(userRepository, times(1)).findById(USER_NOT_FOUND_ID);
    }

    @Test
    void update() {
        when(userRepository.save(any())).thenReturn(Mono.just(getUpdated()));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        userService.update(user, user.getId())
                .subscribe(saved -> assertEquals(getUpdated(), saved));

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        Flux<User> result = userService.getAll();
        assertEquals(users, result.collectList().block());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByUserName() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));

        userService.getUserByUserName(user.getUsername())
                .subscribe(foundUser -> assertEquals(user, foundUser));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }
}