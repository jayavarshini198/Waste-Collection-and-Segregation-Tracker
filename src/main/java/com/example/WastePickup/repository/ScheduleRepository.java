package com.example.WastePickup.repository;

import com.example.WastePickup.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByZoneId(Long zoneId);
    boolean existsByZoneId(Long zoneId);
}
