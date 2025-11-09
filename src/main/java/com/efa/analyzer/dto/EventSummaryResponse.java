package com.efa.analyzer.dto;

public record EventSummaryResponse(
    Integer eventId, long total, long positive, long neutral, long negative) {}
