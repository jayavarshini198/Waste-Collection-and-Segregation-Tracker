package com.example.WastePickup.service;

import com.example.WastePickup.dto.PickupLogRequest;
import com.example.WastePickup.dto.PickupLogResponse;
import com.example.WastePickup.dto.ZoneScoreResponse;
import com.example.WastePickup.entity.Household;
import com.example.WastePickup.entity.PickupLog;
import com.example.WastePickup.entity.Schedule;
import com.example.WastePickup.entity.Zone;
import com.example.WastePickup.exception.BusinessRuleException;
import com.example.WastePickup.exception.ResourceNotFoundException;
import com.example.WastePickup.repository.HouseholdRepository;
import com.example.WastePickup.repository.PickupLogRepository;
import com.example.WastePickup.repository.ScheduleRepository;
import com.example.WastePickup.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PickupLogService {
    private final PickupLogRepository pickupLogRepository;
    private final HouseholdRepository householdRepository;
    private final ScheduleRepository scheduleRepository;
    private final ZoneRepository zoneRepository;

    @Value("${app.scoring.minimum-average:60}")
    private double minimumAverage;

    @Value("${app.scoring.rolling-pickups:5}")
    private int rollingPickups;

    public PickupLogService(PickupLogRepository pickupLogRepository, HouseholdRepository householdRepository,
                            ScheduleRepository scheduleRepository, ZoneRepository zoneRepository) {
        this.pickupLogRepository = pickupLogRepository;
        this.householdRepository = householdRepository;
        this.scheduleRepository = scheduleRepository;
        this.zoneRepository = zoneRepository;
    }

    @Transactional(readOnly = true)
    public List<PickupLogResponse> getAll() {
        return pickupLogRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PickupLogResponse getById(Long id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public List<PickupLogResponse> getByHousehold(Long householdId) {
        if (!householdRepository.existsById(householdId))
            throw new ResourceNotFoundException("Household not found: " + householdId);
        return pickupLogRepository.findByHouseholdIdOrderByPickupTimeDesc(householdId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public PickupLogResponse create(PickupLogRequest request) {
        Household household = householdRepository.findById(request.householdId())
                .orElseThrow(() -> new ResourceNotFoundException("Household not found: " + request.householdId()));
        Zone zone = household.getZone();
        Schedule schedule = scheduleRepository.findByZoneId(zone.getId())
                .orElseThrow(() -> new BusinessRuleException("No pickup schedule is configured for zone " + zone.getName() + "."));

        LocalDateTime pickupTime = request.pickupTime() == null ? LocalDateTime.now() : request.pickupTime();
        validateWithinSchedule(schedule, pickupTime);

        PickupLog log = new PickupLog(null, household, schedule, request.collectorName().trim(), pickupTime,
                request.segregationScore(), false);
        PickupLog saved = pickupLogRepository.save(log);

        double average = calculateRollingAverage(household.getId());
        boolean flagged = average < minimumAverage;
        household.setAverageScore(round2(average));
        household.setReminderFlagged(flagged);
        household.setLastEvaluatedAt(LocalDateTime.now());
        householdRepository.save(household);

        saved.setFlaggedAfterPickup(flagged);
        pickupLogRepository.save(saved);
        System.out.println("[WastePickup] Household " + household.getHouseNumber() +
                " average=" + round2(average) + ", reminderFlagged=" + flagged);
        return toResponse(saved, round2(average));
    }

    @Transactional
    public PickupLogResponse update(Long id, PickupLogRequest request) {
        PickupLog existing = find(id);
        Household oldHousehold = existing.getHousehold();
        Household household = householdRepository.findById(request.householdId())
                .orElseThrow(() -> new ResourceNotFoundException("Household not found: " + request.householdId()));
        Schedule schedule = scheduleRepository.findByZoneId(household.getZone().getId())
                .orElseThrow(() -> new BusinessRuleException("No pickup schedule is configured for zone " + household.getZone().getName() + "."));
        LocalDateTime pickupTime = request.pickupTime() == null ? existing.getPickupTime() : request.pickupTime();
        validateWithinSchedule(schedule, pickupTime);

        existing.setHousehold(household);
        existing.setSchedule(schedule);
        existing.setCollectorName(request.collectorName().trim());
        existing.setPickupTime(pickupTime);
        existing.setSegregationScore(request.segregationScore());
        PickupLog saved = pickupLogRepository.save(existing);

        recalculateHousehold(household);
        saved.setFlaggedAfterPickup(household.getReminderFlagged());
        pickupLogRepository.save(saved);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        PickupLog log = find(id);
        Household household = log.getHousehold();
        pickupLogRepository.delete(log);
        pickupLogRepository.flush();
        recalculateHousehold(household);
    }

    @Transactional(readOnly = true)
    public List<ZoneScoreResponse> getZoneScores() {
        return zoneRepository.findAll().stream().map(zone -> {
            Double avg = pickupLogRepository.averageScoreByZone(zone.getId());
            List<Household> households = householdRepository.findByZoneId(zone.getId());
            long flagged = households.stream().filter(h -> Boolean.TRUE.equals(h.getReminderFlagged())).count();
            return new ZoneScoreResponse(zone.getId(), zone.getName(), zone.getCode(), avg == null ? 0.0 : round2(avg), households.size(), flagged);
        }).toList();
    }

    @Transactional(readOnly = true)
    public double getHouseholdAverage(Long householdId) {
        if (!householdRepository.existsById(householdId))
            throw new ResourceNotFoundException("Household not found: " + householdId);
        return round2(calculateRollingAverage(householdId));
    }

    private void validateWithinSchedule(Schedule schedule, LocalDateTime pickupTime) {
        DayOfWeek day = pickupTime.getDayOfWeek();
        LocalTime time = pickupTime.toLocalTime();
        if (day != schedule.getPickupDay() || time.isBefore(schedule.getStartTime()) || time.isAfter(schedule.getEndTime())) {
            throw new BusinessRuleException("Pickup rejected. Zone " + schedule.getZone().getCode() +
                    " is scheduled on " + schedule.getPickupDay() + " between " + schedule.getStartTime() +
                    " and " + schedule.getEndTime() + ". Requested time: " + pickupTime + ".");
        }
    }

    private double calculateRollingAverage(Long householdId) {
        List<PickupLog> logs = pickupLogRepository.findRecentForHousehold(householdId);
        return logs.stream().limit(Math.max(1, rollingPickups))
                .mapToInt(PickupLog::getSegregationScore).average().orElse(0.0);
    }

    private void recalculateHousehold(Household household) {
        double average = calculateRollingAverage(household.getId());
        household.setAverageScore(round2(average));
        household.setReminderFlagged(average < minimumAverage && average > 0);
        household.setLastEvaluatedAt(LocalDateTime.now());
        householdRepository.save(household);
    }

    private PickupLog find(Long id) {
        return pickupLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pickup log not found: " + id));
    }

    private PickupLogResponse toResponse(PickupLog p) {
        return toResponse(p, p.getHousehold().getAverageScore() == null ? 0.0 : p.getHousehold().getAverageScore());
    }

    private PickupLogResponse toResponse(PickupLog p, double average) {
        Household h = p.getHousehold();
        return new PickupLogResponse(p.getId(), h.getId(), h.getHouseNumber(), h.getResidentName(),
                h.getZone().getId(), h.getZone().getName(), p.getSchedule().getId(), p.getCollectorName(),
                p.getPickupTime(), p.getSegregationScore(), average, p.getFlaggedAfterPickup());
    }

    private double round2(double value) { return Math.round(value * 100.0) / 100.0; }
}
