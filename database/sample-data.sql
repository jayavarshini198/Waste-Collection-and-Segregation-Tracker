USE WastePickup;

INSERT INTO zones(name, code, active) VALUES
('North Zone', 'Z01', TRUE),
('South Zone', 'Z02', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO schedules(zone_id, pickup_day, start_time, end_time)
SELECT id, 'MONDAY', '08:00:00', '11:00:00' FROM zones WHERE code='Z01'
ON DUPLICATE KEY UPDATE pickup_day=VALUES(pickup_day), start_time=VALUES(start_time), end_time=VALUES(end_time);

INSERT INTO schedules(zone_id, pickup_day, start_time, end_time)
SELECT id, 'WEDNESDAY', '08:00:00', '11:00:00' FROM zones WHERE code='Z02'
ON DUPLICATE KEY UPDATE pickup_day=VALUES(pickup_day), start_time=VALUES(start_time), end_time=VALUES(end_time);

INSERT INTO households(house_number, resident_name, phone, zone_id)
SELECT '101', 'Sample Resident 1', '9876500001', id FROM zones WHERE code='Z01'
AND NOT EXISTS (SELECT 1 FROM households h WHERE h.house_number='101' AND h.zone_id=zones.id);

INSERT INTO households(house_number, resident_name, phone, zone_id)
SELECT '202', 'Sample Resident 2', '9876500002', id FROM zones WHERE code='Z02'
AND NOT EXISTS (SELECT 1 FROM households h WHERE h.house_number='202' AND h.zone_id=zones.id);
