package com.efa.analyzer.backend.mapper;

import com.efa.analyzer.backend.dto.FeedbackCreateRequest;
import com.efa.analyzer.backend.dto.FeedbackResponse;
import com.efa.analyzer.backend.model.Event;
import com.efa.analyzer.backend.model.Feedback;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

  @Mapping(target = "eventId", source = "event.id")
  FeedbackResponse toResponse(Feedback feedback);

  List<FeedbackResponse> toResponseList(List<Feedback> feedbackList);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "event", source = "event")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "sentiment", ignore = true)
  Feedback toEntity(FeedbackCreateRequest request, Event event);
}
