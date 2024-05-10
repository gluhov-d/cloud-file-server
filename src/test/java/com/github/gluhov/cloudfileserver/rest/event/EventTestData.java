package com.github.gluhov.cloudfileserver.rest.event;

import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.model.Status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.*;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.*;

public class EventTestData {
    public static final long EVENT_ID = 1;
    public static final long EVENT_NOT_FOUND_ID = 100;
    public static final Event eventAdmin = new Event(EVENT_ID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "1", "1", admin, USER_ID ,FILE_ID, fileAdmin);
    public static final Event eventUser = new Event(EVENT_ID + 1, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "2", "2", user, USER_ID + 1 ,FILE_ID + 1, fileUser);
    public static final Event eventModerator = new Event(EVENT_ID + 2, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "3", "3", moderator, USER_ID + 2,FILE_ID + 2, fileModerator);

    public static final List<Event> userEvents = Arrays.asList(eventUser);

    public static Event getUpdated() {
        return new Event(EVENT_ID + 1, Status.DELETED, LocalDateTime.now(), LocalDateTime.now(), "2", "2", user, USER_ID + 1 ,FILE_ID + 1, fileUser);
    }
}
