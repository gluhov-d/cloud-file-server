package com.github.gluhov.cloudfileserver.repository;

import com.github.gluhov.cloudfileserver.model.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FileEntityRepository extends R2dbcRepository<FileEntity, Long> {
}