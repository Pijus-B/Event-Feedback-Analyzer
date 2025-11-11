package com.efa.analyzer.backend.service.feedbackService;

import com.efa.analyzer.backend.dto.FeedbackCreateRequest;
import com.efa.analyzer.backend.dto.FeedbackResponse;
import com.efa.analyzer.backend.enums.Sentiment;
import com.efa.analyzer.backend.exception.ResourceNotFoundException;
import com.efa.analyzer.backend.mapper.FeedbackMapper;
import com.efa.analyzer.backend.model.Event;
import com.efa.analyzer.backend.model.Feedback;
import com.efa.analyzer.backend.repository.EventRepository;
import com.efa.analyzer.backend.repository.FeedbackRepository;
import com.efa.analyzer.backend.service.SentimentAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackCommandService {

  private final FeedbackRepository feedbackRepository;
  private final EventRepository eventRepository;
  private final FeedbackMapper feedbackMapper;
  private final SentimentAnalysisService sentimentAnalysisService;

  public FeedbackCommandService(
      FeedbackRepository feedbackRepository,
      EventRepository eventRepository,
      FeedbackMapper feedbackMapper,
      SentimentAnalysisService sentimentAnalysisService) {
    this.feedbackRepository = feedbackRepository;
    this.eventRepository = eventRepository;
    this.feedbackMapper = feedbackMapper;
    this.sentimentAnalysisService = sentimentAnalysisService;
  }

  public FeedbackResponse create(Integer eventId, FeedbackCreateRequest request) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Event not found with id: " + eventId));

    Feedback feedback = feedbackMapper.toEntity(request, event);

    Sentiment sentiment = sentimentAnalysisService.analyze(request.content());
    feedback.setSentiment(sentiment);

    Feedback saved = feedbackRepository.save(feedback);
    return feedbackMapper.toResponse(saved);
  }
}
