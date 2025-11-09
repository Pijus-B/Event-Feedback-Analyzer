package com.efa.analyzer.dto;

import com.efa.analyzer.enums.Sentiment;
import java.time.OffsetDateTime;

public record FeedbackResponse(
    Integer id, Integer eventId, String content, Sentiment sentiment, OffsetDateTime createdAt) {}
