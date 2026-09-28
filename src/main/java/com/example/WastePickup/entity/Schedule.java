package com.example.WastePickup.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "schedules", uniqueConstraints = @UniqueConstraint(name = "uk_schedule_zone", columnNames = "zone_id"))
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false, unique = true)
    private Zone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "pickup_day", nullable = false, length = 15)
    private DayOfWeek pickupDay;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public Schedule() {}

    public Schedule(Long id, Zone zone, DayOfWeek pickupDay, LocalTime startTime, LocalTime endTime) {
        this.id=id; this.zone=zone; this.pickupDay=pickupDay; this.startTime=startTime; this.endTime=endTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
    public DayOfWeek getPickupDay() { return pickupDay; }
    public void setPickupDay(DayOfWeek pickupDay) { this.pickupDay = pickupDay; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
