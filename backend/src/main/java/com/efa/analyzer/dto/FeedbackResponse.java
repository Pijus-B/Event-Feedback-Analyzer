package com.efa.analyzer.backend.dto;

import com.efa.analyzer.backend.enums.Sentiment;
import java.time.OffsetDateTime;

public record FeedbackResponse(
    Integer id, Integer eventId, String content, Sentiment sentiment, OffsetDateTime createdAt) {}
