package com.efa.analyzer.service.feedbackService;

import com.efa.analyzer.dto.FeedbackCreateRequest;
import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.enums.Sentiment;
import com.efa.analyzer.exception.ResourceNotFoundException;
import com.efa.analyzer.mapper.FeedbackMapper;
import com.efa.analyzer.model.Event;
import com.efa.analyzer.model.Feedback;
import com.efa.analyzer.repository.EventRepository;
import com.efa.analyzer.repository.FeedbackRepository;
import com.efa.analyzer.service.SentimentAnalysisService;
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

    Sentiment sentiment = sentimentAnalysisService.analyzeSentiment(request.content());
    feedback.setSentiment(sentiment);

    Feedback saved = feedbackRepository.save(feedback);
    return feedbackMapper.toResponse(saved);
  }
}
