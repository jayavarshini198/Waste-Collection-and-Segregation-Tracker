package com.example.WastePickup.repository;

import com.example.WastePickup.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByZoneId(Long zoneId);
    List<Household> findByReminderFlaggedTrue();
    boolean existsByHouseNumberIgnoreCaseAndZoneId(String houseNumber, Long zoneId);
}
