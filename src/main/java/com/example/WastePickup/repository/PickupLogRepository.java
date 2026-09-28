package com.example.WastePickup.repository;

import com.example.WastePickup.entity.PickupLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PickupLogRepository extends JpaRepository<PickupLog, Long> {
    List<PickupLog> findByHouseholdIdOrderByPickupTimeDesc(Long householdId);
    List<PickupLog> findByScheduleZoneIdOrderByPickupTimeDesc(Long zoneId);
    boolean existsByHouseholdId(Long householdId);
    boolean existsByScheduleId(Long scheduleId);

    @Query("select p from PickupLog p where p.household.id = :householdId order by p.pickupTime desc")
    List<PickupLog> findRecentForHousehold(@Param("householdId") Long householdId);

    @Query("select avg(p.segregationScore) from PickupLog p where p.household.zone.id = :zoneId")
    Double averageScoreByZone(@Param("zoneId") Long zoneId);
}
