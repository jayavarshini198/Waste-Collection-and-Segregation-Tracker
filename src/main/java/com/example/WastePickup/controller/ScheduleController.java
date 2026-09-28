package com.example.WastePickup.controller;

import com.example.WastePickup.dto.ScheduleRequest;
import com.example.WastePickup.dto.ScheduleResponse;
import com.example.WastePickup.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
@Tag(name = "Schedules", description = "Manage one pickup schedule per zone")
public class ScheduleController {
    private final ScheduleService service;
    public ScheduleController(ScheduleService service) { this.service = service; }

    @GetMapping
    public List<ScheduleResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ScheduleResponse getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse create(@Valid @RequestBody ScheduleRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public ScheduleResponse update(@PathVariable Long id, @Valid @RequestBody ScheduleRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
