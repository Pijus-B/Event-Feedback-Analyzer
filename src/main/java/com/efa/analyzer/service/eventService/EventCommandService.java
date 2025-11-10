package com.efa.analyzer.service.eventService;

import com.efa.analyzer.dto.EventCreateRequest;
import com.efa.analyzer.model.Event;
import com.efa.analyzer.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventCommandService {

  private final EventRepository eventRepository;

  public EventCommandService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public Integer create(EventCreateRequest request) {
    Event event = new Event().setTitle(request.title()).setDescription(request.description());
    return eventRepository.save(event).getId();
  }
}
