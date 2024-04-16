package com.github.gluhov.cloudfileserver.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileEntityDto extends BaseDto {
    private String location;
}