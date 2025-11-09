package com.efa.analyzer.dto;

import jakarta.validation.constraints.NotBlank;

public record FeedbackCreateRequest(@NotBlank String content) {}
