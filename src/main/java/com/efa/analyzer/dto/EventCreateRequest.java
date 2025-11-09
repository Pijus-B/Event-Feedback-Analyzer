package com.efa.analyzer.dto;

import jakarta.validation.constraints.NotBlank;

public record EventCreateRequest(@NotBlank String name, String description) {}
