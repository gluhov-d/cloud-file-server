package com.github.gluhov.cloudfileserver.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.github.gluhov.cloudfileserver.exception.EntityNotFoundException;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.repository.EventRepository;
import com.github.gluhov.cloudfileserver.repository.FileEntityRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import com.github.gluhov.cloudfileserver.service.FileEntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import static com.github.gluhov.cloudfileserver.rest.event.EventTestData.eventUser;
import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.*;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.admin;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class FileEntityServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private FileEntityRepository fileEntityRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileEntityService fileEntityService;


    @Test
    void testUploadFileToS3() throws MalformedURLException {
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn(fileUser.getName());
        when(filePart.transferTo((File) any())).thenReturn(Mono.empty());

        when(fileEntityRepository.save(any())).thenReturn(Mono.just(fileUser));
        when(eventRepository.save(any())).thenReturn(Mono.just(eventUser));
        when(userRepository.findById(fileUser.getUserId())).thenReturn(Mono.just(user));
        when(amazonS3.putObject(any(), any(), (String) any())).thenReturn(null);
        when(amazonS3.getUrl(any(), any())).thenReturn(URI.create(fileUser.getLocation()).toURL());

        fileEntityService.uploadFileToS3(filePart, fileUser.getUserId()).subscribe();
        verify(fileEntityRepository, times(1)).save(any());
    }

    @Test
    void getById() {
        when(fileEntityRepository.findById(fileUser.getId())).thenReturn(Mono.just(fileUser));

        fileEntityService.getById(fileUser.getId()).subscribe();
        verify(fileEntityRepository, times(1)).findById(fileUser.getId());
    }

    @Test
    void getNotFoundById() {
        when(fileEntityRepository.findById(FILE_NOT_FOUND_ID)).thenReturn(Mono.empty());
        fileEntityService.getById(FILE_NOT_FOUND_ID).subscribe(entity -> fail("Expected an EntityNotFoundException to be thrown"),
                error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("File not found", error.getMessage());
                    assertEquals("CFS_FILE_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                });

        verify(fileEntityRepository, times(1)).findById(FILE_NOT_FOUND_ID);
    }

    @Test
    void deleteById() {
        when(fileEntityRepository.findById(fileUser.getId())).thenReturn(Mono.just(fileUser));
        when(fileEntityRepository.save(fileUser)).thenReturn(Mono.just(fileUser));
        fileEntityService.delete(fileUser.getId(), fileUser.getUserId()).subscribe();
        verify(fileEntityRepository, times(1)).save(any());
    }

    @Test
    void getAllByUserId() {
        when(fileEntityRepository.getAllActiveByUserId(fileAdmin.getUserId())).thenReturn(Flux.fromIterable(adminFiles));
        when(userRepository.findById(fileAdmin.getId())).thenReturn(Mono.just(admin));

        Flux<FileEntity> result = fileEntityService.getAllByUserId(fileAdmin.getUserId());
        assertEquals(adminFiles, result.collectList().block());

        verify(fileEntityRepository, times(1)).getAllActiveByUserId(fileAdmin.getUserId());
    }

    @Test
    void update() {
        when(userRepository.findById(fileUser.getUserId())).thenReturn(Mono.just(user));
        when(fileEntityRepository.save(any())).thenReturn(Mono.just(getUpdated()));
        when(fileEntityRepository.findById(fileUser.getId())).thenReturn(Mono.just(fileUser));

        fileEntityService.update(fileUser, fileUser.getUserId()).subscribe();
        verify(fileEntityRepository, times(1)).save(any());
    }

}
