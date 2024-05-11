package com.github.gluhov.cloudfileserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.gluhov.cloudfileserver.model.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FileEntityDto extends BaseDto{
    private String location;
    private String name;
    private Long userId;

    public FileEntityDto(FileEntity file) {
        super(file.getId(), file.getStatus(), file.getCreatedAt(), file.getUpdatedAt(), file.getCreatedBy(), file.getModifiedBy());
        this.location = file.getLocation();
        this.name = file.getName();
        this.userId = file.getUserId();
    }
}