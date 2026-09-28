package com.example.WastePickup.dto;

public record HouseholdResponse(Long id, String houseNumber, String residentName, String phone,
                                Long zoneId, String zoneName, String zoneCode,
                                Double averageScore, Boolean reminderFlagged) {}
