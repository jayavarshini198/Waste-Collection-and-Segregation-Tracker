package com.example.WastePickup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HouseholdRequest(
        @NotBlank(message = "House number is required") String houseNumber,
        @NotBlank(message = "Resident name is required") String residentName,
        String phone,
        @NotNull(message = "Zone id is required") Long zoneId
) {}
