package com.github.gluhov.cloudfileserver.service;

import com.github.gluhov.cloudfileserver.dto.UserDto;
import com.github.gluhov.cloudfileserver.mapper.UserMapper;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.model.UserRole;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public Mono<User> registerUser(User user) {
        return userRepository.save(
                User.builder()
                        .firstName(user.getFirstName())
                        .username(user.getUsername())
                        .lastName(user.getLastName())
                        .createdBy("1")
                        .modifiedBy("1")
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(Status.ACTIVE)
                        .build()
        ).doOnSuccess(u -> log.info("IN registerUser - user: {} created", u));
    }

    public Mono<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::map);
    }

    public Mono<Void> deleteById(Long id) { return userRepository.deleteById(id);}

    public Mono<User> update(UserDto userDto, Long id) {
        return userRepository.save(
            User.builder()
                    .firstName(userDto.getFirstName())
                    .username(userDto.getUsername())
                    .lastName(userDto.getLastName())
                    .modifiedBy(String.valueOf(id))
                    .updatedAt(LocalDateTime.now())
                    .status(userDto.getStatus())
                    .enabled(userDto.isEnabled())
                    .role(userDto.getRole())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .build()
        ).doOnSuccess(u -> log.info("IN update - user: {} updated", u ));
    }

    public Flux<User> getAll() {

        return userRepository.findAll().
                filter(user -> !user.getStatus().equals(Status.DELETED))
                .sort(Comparator.comparing(User::getLastName).thenComparing(User::getUsername));
    }

    public Mono<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }
}