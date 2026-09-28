package com.example.WastePickup.service;

import com.example.WastePickup.dto.ScheduleRequest;
import com.example.WastePickup.dto.ScheduleResponse;
import com.example.WastePickup.entity.Schedule;
import com.example.WastePickup.entity.Zone;
import com.example.WastePickup.exception.BusinessRuleException;
import com.example.WastePickup.exception.ResourceNotFoundException;
import com.example.WastePickup.repository.PickupLogRepository;
import com.example.WastePickup.repository.ScheduleRepository;
import com.example.WastePickup.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ZoneRepository zoneRepository;
    private final PickupLogRepository pickupLogRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ZoneRepository zoneRepository, PickupLogRepository pickupLogRepository) {
        this.scheduleRepository = scheduleRepository;
        this.zoneRepository = zoneRepository;
        this.pickupLogRepository = pickupLogRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAll() {
        return scheduleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getById(Long id) { return toResponse(find(id)); }

    @Transactional
    public ScheduleResponse create(ScheduleRequest request) {
        validateTimes(request.startTime(), request.endTime());
        Zone zone = zoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + request.zoneId()));
        if (scheduleRepository.existsByZoneId(request.zoneId()))
            throw new BusinessRuleException("A schedule already exists for this zone. Update the existing schedule instead.");
        Schedule schedule = new Schedule(null, zone, request.pickupDay(), request.startTime(), request.endTime());
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponse update(Long id, ScheduleRequest request) {
        validateTimes(request.startTime(), request.endTime());
        Schedule schedule = find(id);
        Zone zone = zoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + request.zoneId()));
        scheduleRepository.findByZoneId(request.zoneId()).filter(s -> !s.getId().equals(id)).ifPresent(s -> {
            throw new BusinessRuleException("A schedule already exists for this zone.");
        });
        schedule.setZone(zone);
        schedule.setPickupDay(request.pickupDay());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = find(id);
        if (pickupLogRepository.existsByScheduleId(id))
            throw new BusinessRuleException("Cannot delete a schedule that has pickup logs.");
        scheduleRepository.delete(schedule);
    }

    public Schedule find(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (!start.isBefore(end))
            throw new BusinessRuleException("Schedule start time must be earlier than end time.");
    }

    private ScheduleResponse toResponse(Schedule s) {
        return new ScheduleResponse(s.getId(), s.getZone().getId(), s.getZone().getName(), s.getZone().getCode(),
                s.getPickupDay(), s.getStartTime(), s.getEndTime());
    }
}
