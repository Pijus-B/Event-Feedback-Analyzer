package com.efa.analyzer.backend.service.feedbackService;

import com.efa.analyzer.backend.dto.FeedbackResponse;
import com.efa.analyzer.backend.exception.ResourceNotFoundException;
import com.efa.analyzer.backend.mapper.FeedbackMapper;
import com.efa.analyzer.backend.model.Feedback;
import com.efa.analyzer.backend.repository.EventRepository;
import com.efa.analyzer.backend.repository.FeedbackRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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

  public List<FeedbackResponse> getAll(Integer eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new ResourceNotFoundException("Event not found with id: " + eventId);
    }
    List<Feedback> list = feedbackRepository.findByEventId(eventId);
    return feedbackMapper.toResponseList(list);
  }

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
