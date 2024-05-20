package com.github.gluhov.cloudfileserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.github.gluhov.cloudfileserver.exception.EntityNotFoundException;
import com.github.gluhov.cloudfileserver.model.Event;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.model.Status;
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
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @Transactional
    public Mono<FileEntity> uploadFileToS3(FilePart filePart, long userId) {
        File file = new File(filePart.filename());
        LocalDateTime now = LocalDateTime.now();
        return filePart.transferTo(file)
                .then(Mono.defer(() -> {
                    return fileEntityRepository.save(
                            FileEntity.builder()
                            .name(filePart.filename())
                            .createdAt(now)
                            .updatedAt(now)
                            .status(Status.ACTIVE)
                            .modifiedBy(String.valueOf(userId))
                            .createdBy(String.valueOf(userId))
                            .userId(userId)
                            .location("https://" + bucket + ".s3.amazonaws.com/" + file.getName())
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
                                    .thenReturn(savedFile));
                }))
                .flatMap(savedFile -> {
                    Mono.fromRunnable(() -> {
                        try {
                            amazonS3.putObject(bucket, filePart.filename(), file);
                        } catch (Exception e) {
                            log.error("Error uploading file to S3: {}", e.getMessage());
                        }
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();

                    Mono.fromRunnable(() -> {
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (IOException e) {
                            log.error("Error deleting tmp file: {}", e.getMessage());
                        }
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                    return Mono.just(savedFile);
                })
                .doOnSuccess(f -> {
                    log.info("IN uploadFileToS3 - file: uploaded");
                })
                .onErrorResume(e -> {
                    log.error("Error occurred during file upload: {}", e.getMessage());
                    if (file.exists() && !file.delete()) {
                        log.error("Failed to delete temporary file: {}", file.getName());
                    }
                    return Mono.error(new RuntimeException("Failed to upload file to S3", e));
                });
    }

    public Mono<FileEntity> getById(long id) {
        return fileEntityRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("File not found", "CFS_FILE_NOT_FOUND")));
    }

    public Mono<Void> delete(long id, long modifiedById) {
        return fileEntityRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("File not found", "CFS_FILE_NOT_FOUND")))
                .flatMap(file -> {
                    file.setStatus(Status.DELETED);
                    file.setUpdatedAt(LocalDateTime.now());
                    file.setModifiedBy(String.valueOf(modifiedById));
                    return fileEntityRepository.save(file).then();
                }).onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }

    public Flux<FileEntity> getAllByUserId(long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found", "CFS_USER_NOT_FOUND")))
                .flatMapMany(u -> fileEntityRepository.getAllActiveByUserId(u.getId()));
    }

    public Mono<FileEntity> update(FileEntity fileEntity, long id) {
        return fileEntityRepository.findById(fileEntity.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("File not found", "CFS_FILE_NOT_FOUND")))
                .flatMap(f -> {
                    f.setStatus(fileEntity.getStatus());
                    f.setUpdatedAt(LocalDateTime.now());
                    f.setModifiedBy(String.valueOf(id));
                    return fileEntityRepository.save(f);
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }
}