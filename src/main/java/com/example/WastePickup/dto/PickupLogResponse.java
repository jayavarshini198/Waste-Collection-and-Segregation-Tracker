package com.example.WastePickup.dto;

import java.time.LocalDateTime;

public record PickupLogResponse(Long id, Long householdId, String houseNumber, String residentName,
                                Long zoneId, String zoneName, Long scheduleId,
                                String collectorName, LocalDateTime pickupTime,
                                Integer segregationScore, Double rollingAverage,
                                Boolean flaggedAfterPickup) {}
