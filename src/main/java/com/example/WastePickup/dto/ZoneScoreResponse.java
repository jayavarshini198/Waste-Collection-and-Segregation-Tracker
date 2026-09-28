package com.example.WastePickup.dto;

public record ZoneScoreResponse(Long zoneId, String zoneName, String zoneCode, Double averageScore,
                                long householdCount, long flaggedHouseholdCount) {}
