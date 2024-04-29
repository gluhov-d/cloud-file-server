package com.github.gluhov.cloudfileserver.rest;

import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import com.github.gluhov.cloudfileserver.security.CustomPrincipal;
import com.github.gluhov.cloudfileserver.service.FileEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@RestController
public class FileRestControllerV1 {

    static final String REST_URL = "/api/v1/files";
    static final String MODERATOR_REST_URL = "/api/v1/moderator/files";
    static final String ADMIN_REST_URL = "/api/v1/admin/files";

    private final FileEntityService fileEntityService;

    @GetMapping(value = REST_URL + "/{id}")
    public Mono<ResponseEntity<FileEntityDto>> get(@PathVariable long id) {
        return fileEntityService.get(id).map(fileEntityDto -> ResponseEntity.ok().body(fileEntityDto));
    }

    @GetMapping(value = REST_URL)
    public Flux<FileEntity> getAll(Authentication authentication){
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return fileEntityService.getAllByUserId(customPrincipal.getId());
    }

    @PutMapping(value = MODERATOR_REST_URL + "/{id}")
    public Mono<FileEntity> update(@RequestBody FileEntityDto fileEntityDto, @PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return fileEntityService.update(fileEntityDto, customPrincipal.getId());
    }

    @DeleteMapping(value = MODERATOR_REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return fileEntityService.delete(id, customPrincipal.getId());
    }

    @DeleteMapping(value = ADMIN_REST_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable long id, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return fileEntityService.delete(id, customPrincipal.getId());
    }

    @PostMapping(value = REST_URL+ "/")
    public Mono<ResponseEntity<FileEntityDto>> upload(@RequestPart("file") Mono<FilePart> filePart, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return filePart.flatMap(f -> fileEntityService.uploadFileToS3(f, customPrincipal.getId())).map(fileEntityDto -> ResponseEntity.ok().body(fileEntityDto));
    }

}