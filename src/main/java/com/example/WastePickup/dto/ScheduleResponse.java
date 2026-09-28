package com.example.WastePickup.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleResponse(Long id, Long zoneId, String zoneName, String zoneCode,
                               DayOfWeek pickupDay, LocalTime startTime, LocalTime endTime) {}
