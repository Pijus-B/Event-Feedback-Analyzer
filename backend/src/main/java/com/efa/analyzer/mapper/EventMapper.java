package com.efa.analyzer.mapper;

import com.efa.analyzer.dto.EventCreateRequest;
import com.efa.analyzer.dto.EventResponse;
import com.efa.analyzer.model.Event;
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
