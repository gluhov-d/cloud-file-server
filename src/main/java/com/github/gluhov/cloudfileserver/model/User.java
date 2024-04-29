package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity{
    private String username;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private boolean enabled;
    @Transient
    private Set<Event> events;

    @Builder
    public User(Long id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy,
                String modifiedBy, String username, String password, UserRole role, String firstName, String lastName,
                boolean enabled) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}