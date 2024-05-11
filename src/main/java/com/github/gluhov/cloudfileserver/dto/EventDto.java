package com.github.gluhov.cloudfileserver.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.gluhov.cloudfileserver.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDto extends BaseDto{
    private Long userId;
    private Long fileId;

    public EventDto(Event event) {
        super(event.getId(), event.getStatus(), event.getCreatedAt(), event.getUpdatedAt(), event.getCreatedBy(), event.getModifiedBy());
        this.userId = event.getUserId();
        this.fileId = event.getFileId();
    }
}