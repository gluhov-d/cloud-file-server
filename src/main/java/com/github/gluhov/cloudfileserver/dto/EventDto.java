package com.github.gluhov.cloudfileserver.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventDto extends BaseDto {
    private UserDto user;
    private FileEntityDto file;
}