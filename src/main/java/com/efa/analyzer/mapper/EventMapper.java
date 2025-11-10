package com.efa.analyzer.mapper;

import com.efa.analyzer.dto.EventCreateRequest;
import com.efa.analyzer.dto.EventResponse;
import com.efa.analyzer.model.Event;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventResponse toResponse(Event event);
    List<EventResponse> toResponseList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event toEntity(EventCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(EventCreateRequest request, @MappingTarget Event target);
}
