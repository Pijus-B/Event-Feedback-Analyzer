package com.efa.analyzer.mapper;

import com.efa.analyzer.dto.FeedbackCreateRequest;
import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.model.Event;
import com.efa.analyzer.model.Feedback;
import org.mapstruct.*;
import java.util.List;

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
