package com.github.gluhov.cloudfileserver.rest.user;

import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.model.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class UserTestData {
    public static final long USER_ID = 1;
    public static final long USER_NOT_FOUND_ID = 100;

    public static final User admin = new User(USER_ID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "", "","admin_user", "WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=", UserRole.ADMIN, "Admin", "User", true);
    public static final User user = new User(USER_ID+1, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "1", "","user_one", "WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=", UserRole.USER, "User", "One", true);
    public static final User moderator = new User(USER_ID+2, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "1", "","moderator_user", "WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=", UserRole.MODERATOR, "Moderator", "User", true);

    public static final List<User> users = Arrays.asList( user, admin, moderator);

    public static User getUpdated() {
        return new User(USER_ID+1, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "1", "", "user_one", "WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=", UserRole.USER, "User", "Updated", true);
    }
}
