package com.example.WastePickup.controller;

import com.example.WastePickup.dto.ZoneRequest;
import com.example.WastePickup.dto.ZoneResponse;
import com.example.WastePickup.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "*")
@Tag(name = "Zones", description = "Manage collection zones")
public class ZoneController {
    private final ZoneService service;
    public ZoneController(ZoneService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "List all zones")
    public List<ZoneResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ZoneResponse getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ZoneResponse create(@Valid @RequestBody ZoneRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public ZoneResponse update(@PathVariable Long id, @Valid @RequestBody ZoneRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
