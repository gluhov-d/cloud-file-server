package com.github.gluhov.cloudfileserver.service;

import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.model.UserRole;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(Status.ACTIVE)
                        .build()
        ).doOnSuccess(u -> log.info("IN registerUser - user: {} created", u));
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }
}