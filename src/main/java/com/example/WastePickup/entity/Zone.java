package com.example.WastePickup.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private Boolean active = true;

    public Zone() {}

    public Zone(Long id, String name, String code, Boolean active) {
        this.id = id; this.name = name; this.code = code; this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
