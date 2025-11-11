package com.efa.analyzer.backend.dto;

public record EventSummaryResponse(
    Integer eventId, long total, long positive, long neutral, long negative) {}
