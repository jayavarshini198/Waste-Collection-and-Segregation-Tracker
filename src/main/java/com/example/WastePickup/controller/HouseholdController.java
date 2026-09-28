package com.example.WastePickup.controller;

import com.example.WastePickup.dto.HouseholdRequest;
import com.example.WastePickup.dto.HouseholdResponse;
import com.example.WastePickup.service.HouseholdService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/households")
@CrossOrigin(origins = "*")
@Tag(name = "Households", description = "Manage households assigned to zones")
public class HouseholdController {
    private final HouseholdService service;
    public HouseholdController(HouseholdService service) { this.service = service; }

    @GetMapping
    public List<HouseholdResponse> getAll(@RequestParam(required = false) Long zoneId,
                                          @RequestParam(required = false) Boolean flagged) {
        return service.getAll(zoneId, flagged);
    }

    @GetMapping("/{id}")
    public HouseholdResponse getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdResponse create(@Valid @RequestBody HouseholdRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public HouseholdResponse update(@PathVariable Long id, @Valid @RequestBody HouseholdRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
