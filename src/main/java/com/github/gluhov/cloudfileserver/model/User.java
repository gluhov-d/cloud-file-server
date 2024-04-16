package com.github.gluhov.cloudfileserver.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class User extends BaseEntity{
    private String username;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Set<Event> events;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}