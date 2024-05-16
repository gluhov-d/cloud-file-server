package com.github.gluhov.cloudfileserver.service.event;

import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.exception.EntityNotFoundException;
import com.github.gluhov.cloudfileserver.mapper.EventMapper;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.repository.EventRepository;
import com.github.gluhov.cloudfileserver.repository.FileEntityRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import com.github.gluhov.cloudfileserver.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.gluhov.cloudfileserver.rest.event.EventTestData.*;
import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.fileUser;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileEntityRepository fileEntityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @Test
    void getAllByUserId() {
        when(eventRepository.getAllActiveByUserId(any())).thenReturn(Flux.fromIterable(userEvents));
        when(userRepository.findById(eventUser.getUserId())).thenReturn(Mono.just(user));
        eventService.getAllByUserId(eventUser.getUserId()).collectList().block();
        verify(eventRepository, times(1)).getAllActiveByUserId(any());
    }

    @Test
    void getByNotFoundId() {
        when(eventRepository.findById(EVENT_NOT_FOUND_ID)).thenReturn(Mono.empty());

        eventService.getById(EVENT_NOT_FOUND_ID).subscribe(entity -> fail("Expected an EntityNotFoundException to be thrown"),
                error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Event not found", error.getMessage());
                    assertEquals("CFS_EVENT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                });
        verify(eventRepository, times(1)).findById(EVENT_NOT_FOUND_ID);
    }

    @Test
    void getById() {
        when(eventRepository.findById(eventUser.getUserId())).thenReturn(Mono.just(eventUser));
        when(eventMapper.map((Event) any())).thenReturn(new EventDto(eventUser));

        eventService.getById(eventUser.getId()).block();
        verify(eventRepository, times(1)).findById(eventUser.getId());
    }

    @Test
    void delete() {
        when(eventRepository.findById(eventUser.getId())).thenReturn(Mono.just(eventUser));
        when(eventRepository.save(any())).thenReturn(Mono.empty());

        eventService.delete(eventUser.getId(), eventUser.getUserId()).block();
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void update() {
        when(userRepository.findById(eventUser.getUserId())).thenReturn(Mono.just(user));
        when(fileEntityRepository.findById(eventUser.getFileId())).thenReturn(Mono.just(fileUser));
        when(eventRepository.findById(eventUser.getId())).thenReturn(Mono.just(eventUser));

        when(eventRepository.save(any())).thenReturn(Mono.just(getUpdated()));

        eventService.update(eventUser, eventUser.getUserId()).block();
        verify(eventRepository, times(1)).save(any());
    }
}
