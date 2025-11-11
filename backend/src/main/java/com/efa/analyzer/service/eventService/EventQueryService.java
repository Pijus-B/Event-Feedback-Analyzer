package com.efa.analyzer.backend.service.eventService;

import com.efa.analyzer.backend.dto.EventResponse;
import com.efa.analyzer.backend.dto.EventSummaryResponse;
import com.efa.analyzer.backend.enums.Sentiment;
import com.efa.analyzer.backend.exception.ResourceNotFoundException;
import com.efa.analyzer.backend.mapper.EventMapper;
import com.efa.analyzer.backend.model.Event;
import com.efa.analyzer.backend.repository.EventRepository;
import com.efa.analyzer.backend.repository.FeedbackRepository;
import jakarta.transaction.Transactional;
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

  @Transactional
  public EventResponse getOne(int id) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    return mapper.toResponse(event);
  }

  @Transactional
  public List<EventResponse> getAll() {
    return mapper.toResponseList(eventRepository.findAll());
  }

  @Transactional
  public List<EventResponse> getAllByTitle(String title) {
    return mapper.toResponseList(eventRepository.findAllByTitle(title.trim()));
  }

  @Transactional
  public EventSummaryResponse getSummary(int eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new ResourceNotFoundException("Event not found with id: " + eventId);
    }
    long positive = feedbackRepository.countByEventIdAndSentiment(eventId, Sentiment.POSITIVE);
    long neutral = feedbackRepository.countByEventIdAndSentiment(eventId, Sentiment.NEUTRAL);
    long negative = feedbackRepository.countByEventIdAndSentiment(eventId, Sentiment.NEGATIVE);
    long total = positive + neutral + negative;

    return new EventSummaryResponse(eventId, total, positive, neutral, negative);
  }
}
