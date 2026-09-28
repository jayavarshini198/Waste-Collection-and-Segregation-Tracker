CREATE DATABASE IF NOT EXISTS WastePickup;
USE WastePickup;

CREATE TABLE IF NOT EXISTS zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_id BIGINT NOT NULL UNIQUE,
    pickup_day VARCHAR(15) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_schedule_zone FOREIGN KEY (zone_id) REFERENCES zones(id)
);

CREATE TABLE IF NOT EXISTS households (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    house_number VARCHAR(50) NOT NULL,
    resident_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20),
    zone_id BIGINT NOT NULL,
    average_score DOUBLE NOT NULL DEFAULT 0,
    reminder_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    last_evaluated_at DATETIME NULL,
    CONSTRAINT fk_household_zone FOREIGN KEY (zone_id) REFERENCES zones(id),
    CONSTRAINT uk_household_zone_number UNIQUE (zone_id, house_number)
);

CREATE TABLE IF NOT EXISTS pickup_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    household_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    collector_name VARCHAR(120) NOT NULL,
    pickup_time DATETIME NOT NULL,
    segregation_score INT NOT NULL,
    flagged_after_pickup BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_pickup_household FOREIGN KEY (household_id) REFERENCES households(id),
    CONSTRAINT fk_pickup_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id),
    CONSTRAINT chk_score CHECK (segregation_score BETWEEN 0 AND 100)
);
