package com.example.WastePickup.dto;

import jakarta.validation.constraints.NotBlank;

public record ZoneRequest(
        @NotBlank(message = "Zone name is required") String name,
        @NotBlank(message = "Zone code is required") String code,
        Boolean active
) {}
