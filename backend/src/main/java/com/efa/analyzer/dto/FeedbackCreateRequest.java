package com.efa.analyzer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackCreateRequest(@NotBlank @Size(max = 2000) String content) {}
