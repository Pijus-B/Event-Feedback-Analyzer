package com.efa.analyzer.service.feedbackService;

import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.exception.ResourceNotFoundException;
import com.efa.analyzer.mapper.FeedbackMapper;
import com.efa.analyzer.model.Feedback;
import com.efa.analyzer.repository.EventRepository;
import com.efa.analyzer.repository.FeedbackRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackQueryService {

  private final FeedbackRepository feedbackRepository;
  private final EventRepository eventRepository;
  private final FeedbackMapper feedbackMapper;

  public FeedbackQueryService(
      FeedbackRepository feedbackRepository,
      EventRepository eventRepository,
      FeedbackMapper feedbackMapper) {
    this.feedbackRepository = feedbackRepository;
    this.eventRepository = eventRepository;
    this.feedbackMapper = feedbackMapper;
  }

  @Transactional
  public List<FeedbackResponse> getAll(Integer eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new ResourceNotFoundException("Event not found with id: " + eventId);
    }
    List<Feedback> list = feedbackRepository.findByEventId(eventId);
    return feedbackMapper.toResponseList(list);
  }

  @Transactional
  public FeedbackResponse getOne(Integer eventId, Integer feedbackId) {
    if (!eventRepository.existsById(eventId)) {
      throw new ResourceNotFoundException("Event not found with id: " + eventId);
    }
    Feedback feedback =
        feedbackRepository
            .findById(feedbackId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));
    if (feedback.getEvent() == null || !eventId.equals(feedback.getEvent().getId())) {
      throw new ResourceNotFoundException(
          "Feedback " + feedbackId + " does not belong to event " + eventId);
    }
    return feedbackMapper.toResponse(feedback);
  }
}
