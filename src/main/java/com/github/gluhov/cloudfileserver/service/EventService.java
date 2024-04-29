package com.github.gluhov.cloudfileserver.service;

import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.mapper.EventMapper;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.repository.EventRepository;
import com.github.gluhov.cloudfileserver.repository.FileEntityRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final FileEntityRepository fileEntityRepository;
    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    public Flux<Event> getAllByUserId(Long id) {
        return eventRepository.getAllByUserId(id)
                .filter(event -> !event.getStatus().equals(Status.DELETED));
    }

    public Mono<EventDto> getById(Long id) {
        return eventRepository.findById(id).map(eventMapper::map);
    }

    public Mono<Void> delete(Long id, Long modifiedById) {
        return eventRepository.findById(id)
                .flatMap(event -> {
                    event.setStatus(Status.DELETED);
                    event.setUpdatedAt(LocalDateTime.now());
                    event.setModifiedBy(String.valueOf(modifiedById));
                    return eventRepository.save(event).then();
                }).onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }

    public Mono<Event> update(EventDto eventDto, Long id) {
        Mono<User> user = userRepository.findById(eventDto.getUserId());
        return user.flatMap(u -> fileEntityRepository.findById(eventDto.getFileId()).flatMap(file -> eventRepository.save(
                Event.builder()
                        .id(eventDto.getId())
                        .file(file)
                        .fileId(file.getId())
                        .user(u)
                        .userId(u.getId())
                        .updatedAt(LocalDateTime.now())
                        .modifiedBy(String.valueOf(id))
                        .status(eventDto.getStatus())
                        .build()
        )));
    }
}