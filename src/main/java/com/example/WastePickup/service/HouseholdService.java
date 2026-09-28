package com.example.WastePickup.service;

import com.example.WastePickup.dto.HouseholdRequest;
import com.example.WastePickup.dto.HouseholdResponse;
import com.example.WastePickup.entity.Household;
import com.example.WastePickup.entity.Zone;
import com.example.WastePickup.exception.BusinessRuleException;
import com.example.WastePickup.exception.ResourceNotFoundException;
import com.example.WastePickup.repository.HouseholdRepository;
import com.example.WastePickup.repository.PickupLogRepository;
import com.example.WastePickup.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HouseholdService {
    private final HouseholdRepository householdRepository;
    private final ZoneRepository zoneRepository;
    private final PickupLogRepository pickupLogRepository;

    public HouseholdService(HouseholdRepository householdRepository, ZoneRepository zoneRepository, PickupLogRepository pickupLogRepository) {
        this.householdRepository = householdRepository;
        this.zoneRepository = zoneRepository;
        this.pickupLogRepository = pickupLogRepository;
    }

    @Transactional(readOnly = true)
    public List<HouseholdResponse> getAll(Long zoneId, Boolean flagged) {
        List<Household> households;
        if (zoneId != null) households = householdRepository.findByZoneId(zoneId);
        else if (Boolean.TRUE.equals(flagged)) households = householdRepository.findByReminderFlaggedTrue();
        else households = householdRepository.findAll();
        return households.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HouseholdResponse getById(Long id) { return toResponse(find(id)); }

    @Transactional
    public HouseholdResponse create(HouseholdRequest request) {
        Zone zone = zoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + request.zoneId()));
        if (householdRepository.existsByHouseNumberIgnoreCaseAndZoneId(request.houseNumber(), request.zoneId()))
            throw new BusinessRuleException("A household with house number " + request.houseNumber() + " already exists in this zone.");
        Household household = new Household(null, request.houseNumber().trim(), request.residentName().trim(), request.phone(), zone,
                0.0, false, null);
        return toResponse(householdRepository.save(household));
    }

    @Transactional
    public HouseholdResponse update(Long id, HouseholdRequest request) {
        Household household = find(id);
        Zone zone = zoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + request.zoneId()));
        household.setHouseNumber(request.houseNumber().trim());
        household.setResidentName(request.residentName().trim());
        household.setPhone(request.phone());
        household.setZone(zone);
        return toResponse(householdRepository.save(household));
    }

    @Transactional
    public void delete(Long id) {
        Household household = find(id);
        if (pickupLogRepository.existsByHouseholdId(id))
            throw new BusinessRuleException("Cannot delete a household that has pickup logs.");
        householdRepository.delete(household);
    }

    public Household find(Long id) {
        return householdRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Household not found: " + id));
    }

    private HouseholdResponse toResponse(Household h) {
        return new HouseholdResponse(h.getId(), h.getHouseNumber(), h.getResidentName(), h.getPhone(),
                h.getZone().getId(), h.getZone().getName(), h.getZone().getCode(), h.getAverageScore(), h.getReminderFlagged());
    }
}
