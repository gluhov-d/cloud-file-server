package com.github.gluhov.cloudfileserver.rest;

import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.FileEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
@RestController
public class FileRestControllerV1 {

    private final FileEntityService fileEntityService;

    @PostMapping(value = "/")
    public Mono<ResponseEntity<FileEntityDto>> upload(@RequestPart("file") Mono<FilePart> filePart, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return filePart.flatMap(f -> fileEntityService.uploadFileToS3(f, customPrincipal.getId())).map(fileEntityDto -> ResponseEntity.ok().body(fileEntityDto));
    }
}