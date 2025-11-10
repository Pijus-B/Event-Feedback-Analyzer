package com.efa.analyzer.mapper;

import com.efa.analyzer.dto.FeedbackCreateRequest;
import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.model.Event;
import com.efa.analyzer.model.Feedback;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

  @Mapping(target = "eventId", source = "event.id")
  FeedbackResponse toResponse(Feedback feedback);

  List<FeedbackResponse> toResponseList(List<Feedback> feedbackList);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "event", source = "event")
  @Mapping(target = "createdAt", ignore = true) // set by @PrePersist
  @Mapping(target = "sentiment", ignore = true) // set in command service after analysis
  Feedback toEntity(FeedbackCreateRequest request, Event event);
}
