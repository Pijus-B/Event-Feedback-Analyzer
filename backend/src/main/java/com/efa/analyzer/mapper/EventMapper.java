package com.efa.analyzer.backend.mapper;

import com.efa.analyzer.backend.dto.EventCreateRequest;
import com.efa.analyzer.backend.dto.EventResponse;
import com.efa.analyzer.backend.model.Event;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

  EventResponse toResponse(Event event);

  List<EventResponse> toResponseList(List<Event> events);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Event toEntity(EventCreateRequest request);
}
