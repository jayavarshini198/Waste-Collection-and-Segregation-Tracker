package com.example.WastePickup.service;

import com.example.WastePickup.dto.ZoneRequest;
import com.example.WastePickup.dto.ZoneResponse;
import com.example.WastePickup.entity.Zone;
import com.example.WastePickup.exception.BusinessRuleException;
import com.example.WastePickup.exception.ResourceNotFoundException;
import com.example.WastePickup.repository.HouseholdRepository;
import com.example.WastePickup.repository.ScheduleRepository;
import com.example.WastePickup.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZoneService {
    private final ZoneRepository zoneRepository;
    private final ScheduleRepository scheduleRepository;
    private final HouseholdRepository householdRepository;

    public ZoneService(ZoneRepository zoneRepository, ScheduleRepository scheduleRepository, HouseholdRepository householdRepository) {
        this.zoneRepository = zoneRepository;
        this.scheduleRepository = scheduleRepository;
        this.householdRepository = householdRepository;
    }

    public List<ZoneResponse> getAll() {
        return zoneRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ZoneResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public ZoneResponse create(ZoneRequest request) {
        if (zoneRepository.existsByCodeIgnoreCase(request.code()))
            throw new BusinessRuleException("Zone code already exists: " + request.code());
        if (zoneRepository.existsByNameIgnoreCase(request.name()))
            throw new BusinessRuleException("Zone name already exists: " + request.name());
        Zone zone = new Zone(null, request.name().trim(), request.code().trim().toUpperCase(), request.active() == null ? true : request.active());
        return toResponse(zoneRepository.save(zone));
    }

    @Transactional
    public ZoneResponse update(Long id, ZoneRequest request) {
        Zone zone = find(id);
        zoneRepository.findByCodeIgnoreCase(request.code()).filter(z -> !z.getId().equals(id)).ifPresent(z -> {
            throw new BusinessRuleException("Zone code already exists: " + request.code());
        });
        zone.setName(request.name().trim());
        zone.setCode(request.code().trim().toUpperCase());
        zone.setActive(request.active() == null ? zone.getActive() : request.active());
        return toResponse(zoneRepository.save(zone));
    }

    @Transactional
    public void delete(Long id) {
        Zone zone = find(id);
        if (scheduleRepository.existsByZoneId(id))
            throw new BusinessRuleException("Cannot delete zone while a pickup schedule exists for it.");
        if (!householdRepository.findByZoneId(id).isEmpty())
            throw new BusinessRuleException("Cannot delete zone while households are assigned to it.");
        zoneRepository.delete(zone);
    }

    public Zone find(Long id) {
        return zoneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + id));
    }

    private ZoneResponse toResponse(Zone z) {
        return new ZoneResponse(z.getId(), z.getName(), z.getCode(), z.getActive());
    }
}
