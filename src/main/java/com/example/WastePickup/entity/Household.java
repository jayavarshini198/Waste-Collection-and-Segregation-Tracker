package com.example.WastePickup.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "households")
public class Household {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "house_number", nullable = false, length = 50)
    private String houseNumber;

    @Column(name = "resident_name", nullable = false, length = 120)
    private String residentName;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "reminder_flagged", nullable = false)
    private Boolean reminderFlagged = false;

    @Column(name = "last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    public Household() {}

    public Household(Long id, String houseNumber, String residentName, String phone, Zone zone,
                      Double averageScore, Boolean reminderFlagged, LocalDateTime lastEvaluatedAt) {
        this.id=id; this.houseNumber=houseNumber; this.residentName=residentName; this.phone=phone;
        this.zone=zone; this.averageScore=averageScore; this.reminderFlagged=reminderFlagged; this.lastEvaluatedAt=lastEvaluatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber=houseNumber; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName=residentName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone=phone; }
    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone=zone; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore=averageScore; }
    public Boolean getReminderFlagged() { return reminderFlagged; }
    public void setReminderFlagged(Boolean reminderFlagged) { this.reminderFlagged=reminderFlagged; }
    public LocalDateTime getLastEvaluatedAt() { return lastEvaluatedAt; }
    public void setLastEvaluatedAt(LocalDateTime lastEvaluatedAt) { this.lastEvaluatedAt=lastEvaluatedAt; }
}
