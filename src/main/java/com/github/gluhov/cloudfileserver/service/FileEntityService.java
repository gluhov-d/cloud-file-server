package com.github.gluhov.cloudfileserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.mapper.FileEntityMapper;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.model.Status;
import com.github.gluhov.cloudfileserver.model.User;
import com.github.gluhov.cloudfileserver.repository.EventRepository;
import com.github.gluhov.cloudfileserver.repository.FileEntityRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileEntityService {
    private final AmazonS3 amazonS3;

    @Value("${s3.bucket}")
    private String bucket;

    private final FileEntityRepository fileEntityRepository;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FileEntityMapper fileEntityMapper;

    @Transactional
    public Mono<FileEntityDto> uploadFileToS3(FilePart filePart, long userId) {
        File file = new File(filePart.filename());
        LocalDateTime now = LocalDateTime.now();
        return filePart.transferTo(file)
                .then(Mono.fromRunnable(() -> {
                    amazonS3.putObject(bucket, filePart.filename(), file);
                    file.delete();
                }))
                .then(Mono.defer(() -> {
                    Mono<String> location = Mono.just(amazonS3.getUrl(bucket, filePart.filename()).toString());
                    return location.flatMap(locationValue -> fileEntityRepository.save(
                            FileEntity.builder()
                            .name(filePart.filename())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .status(Status.ACTIVE)
                            .modifiedBy(String.valueOf(userId))
                            .createdBy(String.valueOf(userId))
                            .userId(userId)
                            .location(locationValue)
                            .build()
                    ).flatMap(savedFile -> userRepository.findById(userId)
                                    .map(user -> Event.builder()
                                            .fileId(savedFile.getId())
                                            .status(Status.ACTIVE)
                                            .updatedAt(now)
                                            .createdAt(now)
                                            .modifiedBy(String.valueOf(user.getId()))
                                            .createdBy(String.valueOf(user.getId()))
                                            .userId(userId)
                                            .build())
                                    .flatMap(eventRepository::save)
                                    .onErrorResume(e -> {
                                        log.error("Error saving event: {}", e.getMessage());
                                        return Mono.error(new RuntimeException("Error saving event"));
                                    })
                                    .thenReturn(savedFile))
                                    .map(fileEntityMapper::map)
                            );

                })).doOnSuccess(f -> log.info("IN uploadFileToS3 - file: {} uploaded", f.getName()))
                .onErrorResume(e -> Mono.error(new RuntimeException(e.getMessage())));

    }

    public Mono<FileEntity> getById(long id) {
        return fileEntityRepository.findById(id);
    }

    public Mono<Void> delete(long id, long modifiedById) {
        return fileEntityRepository.findById(id)
                .flatMap(file -> {
                    file.setStatus(Status.DELETED);
                    file.setUpdatedAt(LocalDateTime.now());
                    file.setModifiedBy(String.valueOf(modifiedById));
                    return fileEntityRepository.save(file).then();
                }).onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }


    // TO-DO change to query
    public Flux<FileEntity> getAllByUserId(long id) {
        return fileEntityRepository.getAllByUserId(id)
                .filter(fileEntity -> !fileEntity.getStatus().equals(Status.DELETED));
    }

    public Mono<FileEntity> update(FileEntityDto fileEntityDto, long id) {
        Mono<User> user = userRepository.findById(fileEntityDto.getUserId());
        return user.flatMap(u -> fileEntityRepository.save(
                FileEntity.builder()
                        .name(fileEntityDto.getName())
                        .id(fileEntityDto.getId())
                        .updatedAt(LocalDateTime.now())
                        .status(fileEntityDto.getStatus())
                        .modifiedBy(String.valueOf(id))
                        .userId(fileEntityDto.getUserId())
                        .build()
        ));
    }
}