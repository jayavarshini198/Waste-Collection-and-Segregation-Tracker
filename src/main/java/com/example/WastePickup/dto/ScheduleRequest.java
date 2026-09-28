package com.example.WastePickup.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull(message = "Zone id is required") Long zoneId,
        @NotNull(message = "Pickup day is required") DayOfWeek pickupDay,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "End time is required") LocalTime endTime
) {}
