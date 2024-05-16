package com.github.gluhov.cloudfileserver.service;

import com.github.gluhov.cloudfileserver.exception.EntityNotFoundException;
import com.github.gluhov.cloudfileserver.exception.UserWithUsernameAlreadyExistsException;
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
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private Mono<Void> checkIfExistsByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (Objects.nonNull(user)) {
                        return Mono.error(new UserWithUsernameAlreadyExistsException("User with defined username already exists.", "CFS_USER_DUPLICATE_USERNAME"));
                    }
                    return Mono.empty();
                });
    }

    public Mono<User> registerUser(User user) {
        return checkIfExistsByUsername(user.getUsername())
                .then(Mono.defer(() -> userRepository.save(
                        User.builder()
                                .firstName(user.getFirstName())
                                .username(user.getUsername())
                                .lastName(user.getLastName())
                                .createdBy("")
                                .modifiedBy("")
                                .password(passwordEncoder.encode(user.getPassword()))
                                .role(UserRole.USER)
                                .enabled(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .status(Status.ACTIVE)
                                .build()
                )));
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<Void> deleteById(Long id, Long modifiedById) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found", "CFS_USER_NOT_FOUND")))
                .flatMap(user -> {
                    user.setStatus(Status.DELETED);
                    user.setModifiedBy(String.valueOf(modifiedById));
                    user.setEnabled(false);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user).then();
                });
    }

    public Mono<User> update(User user, Long modifiedById) {
        return userRepository.findById(user.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found", "CFS_USER_NOT_FOUND")))
                .flatMap(u -> userRepository.save(
                        User.builder()
                                .firstName(user.getFirstName())
                                .username(user.getUsername())
                                .lastName(user.getLastName())
                                .modifiedBy(String.valueOf(modifiedById))
                                .updatedAt(LocalDateTime.now())
                                .status(user.getStatus())
                                .enabled(user.isEnabled())
                                .role(user.getRole())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .build()
                ).doOnSuccess(s -> log.info("IN update - user: {} updated", s )));
    }

    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    public Flux<User> findAllActive() {
        return userRepository.findAllActive();
    }

    public Mono<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found", "CFS_USER_NOT_FOUND")));
    }
}