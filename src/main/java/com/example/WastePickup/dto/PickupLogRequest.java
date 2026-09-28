package com.example.WastePickup.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PickupLogRequest(
        @NotNull(message = "Household id is required") Long householdId,
        @NotBlank(message = "Collector name is required") String collectorName,
        LocalDateTime pickupTime,
        @NotNull(message = "Segregation score is required")
        @Min(value = 0, message = "Segregation score must be between 0 and 100")
        @Max(value = 100, message = "Segregation score must be between 0 and 100")
        Integer segregationScore
) {}
