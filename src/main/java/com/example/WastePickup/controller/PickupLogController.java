package com.example.WastePickup.controller;

import com.example.WastePickup.dto.PickupLogRequest;
import com.example.WastePickup.dto.PickupLogResponse;
import com.example.WastePickup.dto.ZoneScoreResponse;
import com.example.WastePickup.service.PickupLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickups")
@CrossOrigin(origins = "*")
@Tag(name = "Pickup Logs", description = "Record collection events and segregation scores")
public class PickupLogController {
    private final PickupLogService service;
    public PickupLogController(PickupLogService service) { this.service = service; }

    @GetMapping
    public List<PickupLogResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public PickupLogResponse getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/household/{householdId}")
    public List<PickupLogResponse> getByHousehold(@PathVariable Long householdId) { return service.getByHousehold(householdId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a pickup and segregation score",
            description = "The service rejects the request unless the pickup timestamp is inside the household zone's scheduled day/time window.")
    public PickupLogResponse create(@Valid @RequestBody PickupLogRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public PickupLogResponse update(@PathVariable Long id, @Valid @RequestBody PickupLogRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/household/{householdId}/average")
    public Map<String, Object> householdAverage(@PathVariable Long householdId) {
        return Map.of("householdId", householdId, "rollingAverage", service.getHouseholdAverage(householdId));
    }

    @GetMapping("/reports/zone-scores")
    @Operation(summary = "View zone-wise average segregation score")
    public List<ZoneScoreResponse> zoneScores() { return service.getZoneScores(); }
}
