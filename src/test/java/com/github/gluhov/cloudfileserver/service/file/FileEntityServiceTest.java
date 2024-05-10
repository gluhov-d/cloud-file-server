package com.github.gluhov.cloudfileserver.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.repository.FileEntityRepository;
import com.github.gluhov.cloudfileserver.repository.UserRepository;
import com.github.gluhov.cloudfileserver.service.FileEntityService;
import org.junit.jupiter.api.Assertions;
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

import static com.github.gluhov.cloudfileserver.rest.file.FileEntityTestData.*;
import static com.github.gluhov.cloudfileserver.rest.user.UserTestData.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class FileEntityServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private FileEntityRepository fileEntityRepository;

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
        fileEntityService.getById(FILE_NOT_FOUND_ID).subscribe(Assertions::assertNotNull);

        verify(fileEntityRepository, times(1)).findById(FILE_NOT_FOUND_ID);
    }

    @Test
    void deleteById() {
        when(fileEntityRepository.findById(fileUser.getId())).thenReturn(Mono.just(fileUser));

        fileEntityService.delete(fileUser.getId(), fileUser.getUserId()).subscribe();
        verify(fileEntityRepository, times(1)).save(any());
    }

    @Test
    void getAllByUserId() {
        when(fileEntityRepository.getAllByUserId(fileUser.getUserId())).thenReturn(Flux.fromIterable(userFiles));

        Flux<FileEntity> result = fileEntityService.getAllByUserId(fileUser.getUserId());
        assertEquals(userFiles, result.collectList().block());

        verify(fileEntityRepository, times(1)).getAllByUserId(fileUser.getUserId());
    }

    @Test
    void update() {
        when(userRepository.findById(fileUser.getUserId())).thenReturn(Mono.just(user));
        when(fileEntityRepository.save(any())).thenReturn(Mono.just(getUpdated()));

        fileEntityService.update(new FileEntityDto(fileUser), fileUser.getUserId()).subscribe();
        verify(fileEntityRepository, times(1)).save(any());
    }

}
