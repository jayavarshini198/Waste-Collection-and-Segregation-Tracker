package com.example.WastePickup.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_logs")
public class PickupLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "collector_name", nullable = false, length = 120)
    private String collectorName;

    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;

    @Column(name = "segregation_score", nullable = false)
    private Integer segregationScore;

    @Column(name = "flagged_after_pickup", nullable = false)
    private Boolean flaggedAfterPickup = false;

    public PickupLog() {}

    public PickupLog(Long id, Household household, Schedule schedule, String collectorName,
                     LocalDateTime pickupTime, Integer segregationScore, Boolean flaggedAfterPickup) {
        this.id=id; this.household=household; this.schedule=schedule; this.collectorName=collectorName;
        this.pickupTime=pickupTime; this.segregationScore=segregationScore; this.flaggedAfterPickup=flaggedAfterPickup;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public Household getHousehold() { return household; }
    public void setHousehold(Household household) { this.household=household; }
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule=schedule; }
    public String getCollectorName() { return collectorName; }
    public void setCollectorName(String collectorName) { this.collectorName=collectorName; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime=pickupTime; }
    public Integer getSegregationScore() { return segregationScore; }
    public void setSegregationScore(Integer segregationScore) { this.segregationScore=segregationScore; }
    public Boolean getFlaggedAfterPickup() { return flaggedAfterPickup; }
    public void setFlaggedAfterPickup(Boolean flaggedAfterPickup) { this.flaggedAfterPickup=flaggedAfterPickup; }
}
