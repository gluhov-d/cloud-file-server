package com.github.gluhov.cloudfileserver.mapper;

import com.github.gluhov.cloudfileserver.dto.FileEntityDto;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    FileEntityDto map(FileEntity file);

    @InheritInverseConfiguration
    FileEntity map(FileEntityDto fileEntityDto);
}