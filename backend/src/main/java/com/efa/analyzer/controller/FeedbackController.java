package com.efa.analyzer.controller;

import com.efa.analyzer.dto.FeedbackCreateRequest;
import com.efa.analyzer.dto.FeedbackResponse;
import com.efa.analyzer.service.feedbackService.FeedbackCommandService;
import com.efa.analyzer.service.feedbackService.FeedbackQueryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/feedback")
public class FeedbackController {

  private final FeedbackCommandService feedbackCommandService;
  private final FeedbackQueryService feedbackQueryService;

  public FeedbackController(
      FeedbackCommandService feedbackCommandService, FeedbackQueryService feedbackQueryService) {
    this.feedbackCommandService = feedbackCommandService;
    this.feedbackQueryService = feedbackQueryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeedbackResponse create(
      @PathVariable Integer eventId,
      @Valid @RequestBody FeedbackCreateRequest feedbackCreateRequest) {
    return feedbackCommandService.create(eventId, feedbackCreateRequest);
  }

  @GetMapping
  public List<FeedbackResponse> list(@PathVariable Integer eventId) {
    return feedbackQueryService.getAll(eventId);
  }

  @GetMapping("/{feedbackId}")
  public FeedbackResponse getOne(@PathVariable Integer eventId, @PathVariable Integer feedbackId) {
    return feedbackQueryService.getOne(eventId, feedbackId);
  }
}
