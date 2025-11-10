package com.efa.analyzer.service.eventService;

import com.efa.analyzer.dto.EventResponse;
import com.efa.analyzer.dto.EventSummaryResponse;
import com.efa.analyzer.enums.Sentiment;
import com.efa.analyzer.exception.ResourceNotFoundException;
import com.efa.analyzer.mapper.EventMapper;
import com.efa.analyzer.model.Event;
import com.efa.analyzer.repository.EventRepository;
import com.efa.analyzer.repository.FeedbackRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventQueryService {

  private final EventRepository eventRepository;
  private final FeedbackRepository feedbackRepository;
  private final EventMapper mapper;

  public EventQueryService(
      EventRepository eventRepository, FeedbackRepository feedbackRepository, EventMapper mapper) {
    this.eventRepository = eventRepository;
    this.feedbackRepository = feedbackRepository;
    this.mapper = mapper;
  }

  public EventResponse getOne(int id) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    return mapper.toResponse(event);
  }

  public List<EventResponse> getAll() {
    return mapper.toResponseList(eventRepository.findAll());
  }

  public EventSummaryResponse getSummary(int eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new ResourceNotFoundException("Event not found with id: " + eventId);
    }
    long positive = feedbackRepository.countByEvent_IdAndSentiment(eventId, Sentiment.POSITIVE);
    long neutral = feedbackRepository.countByEvent_IdAndSentiment(eventId, Sentiment.NEUTRAL);
    long negative = feedbackRepository.countByEvent_IdAndSentiment(eventId, Sentiment.NEGATIVE);
    long total = positive + neutral + negative;
    return new EventSummaryResponse(eventId, total, positive, neutral, negative);
  }
}
