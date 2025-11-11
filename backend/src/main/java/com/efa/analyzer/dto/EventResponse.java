package com.efa.analyzer.backend.dto;

import java.time.OffsetDateTime;

public record EventResponse(
    Integer id, String title, String description, OffsetDateTime createdAt) {}
