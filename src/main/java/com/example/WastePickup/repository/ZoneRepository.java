package com.example.WastePickup.repository;

import com.example.WastePickup.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
    Optional<Zone> findByCodeIgnoreCase(String code);
}
