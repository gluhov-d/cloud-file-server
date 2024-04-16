package com.github.gluhov.cloudfileserver.mapper;

import com.github.gluhov.cloudfileserver.dto.EventDto;
import com.github.gluhov.cloudfileserver.model.Event;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto map(Event event);

    @InheritInverseConfiguration
    Event map(EventDto eventDto);
}