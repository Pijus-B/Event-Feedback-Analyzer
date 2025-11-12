package com.efa.analyzer.controller;

import com.efa.analyzer.dto.EventCreateRequest;
import com.efa.analyzer.dto.EventResponse;
import com.efa.analyzer.dto.EventSummaryResponse;
import com.efa.analyzer.service.eventService.EventCommandService;
import com.efa.analyzer.service.eventService.EventQueryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventCommandService eventCommandService;
    private final EventQueryService eventQueryService;

    public EventController(
            EventCommandService eventCommandService, EventQueryService eventQueryService) {
        this.eventCommandService = eventCommandService;
        this.eventQueryService = eventQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer create(@Valid @RequestBody EventCreateRequest eventCreateRequest) {
        return eventCommandService.create(eventCreateRequest);
    }

    @GetMapping
    public List<EventResponse> list() {
        return eventQueryService.getAll();
    }

    @GetMapping(params = "title")
    public List<EventResponse> getByTitle(@RequestParam String title)
    {
        return eventQueryService.getAllByTitle(title);
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable Integer id) {
        return eventQueryService.getOne(id);
    }

    @GetMapping("/{id}/summary")
    public EventSummaryResponse summary(@PathVariable Integer id) {
        return eventQueryService.getSummary(id);
    }
}