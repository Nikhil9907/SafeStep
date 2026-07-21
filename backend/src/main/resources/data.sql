-- Clear existing data to avoid duplicate key issues on multiple restarts
TRUNCATE TABLE cctv_data CASCADE;
TRUNCATE TABLE lighting_data CASCADE;
TRUNCATE TABLE crime_data CASCADE;

-- Seed CCTV data (Active cameras near Delhi baseline 28.6139, 77.2090)
INSERT INTO cctv_data (id, latitude, longitude, status) VALUES 
(1, 28.6145, 77.2095, 'ACTIVE'),
(2, 28.6160, 77.2110, 'ACTIVE'),
(3, 28.6190, 77.2140, 'ACTIVE');

-- Seed Streetlight data (Working lights near Delhi baseline)
INSERT INTO lighting_data (id, latitude, longitude, brightness, status) VALUES 
(1, 28.6142, 77.2092, 'HIGH', 'WORKING'),
(2, 28.6175, 77.2115, 'MEDIUM', 'WORKING'),
(3, 28.6195, 77.2145, 'HIGH', 'WORKING');

-- Seed Crime Data near Delhi baseline
INSERT INTO crime_data (id, latitude, longitude, title, description, severity, reported_at) VALUES 
(1, 28.6150, 77.2100, 'Theft near metro gate', 'Pickpocketing reported at metro entry', 'MEDIUM', NOW()),
(2, 28.6180, 77.2120, 'Vandalism at street corner', 'Broken bench and graffiti', 'LOW', NOW());

-- Reset identity sequence counters so subsequent manual/API inserts don't collide
ALTER SEQUENCE cctv_data_id_seq RESTART WITH 4;
ALTER SEQUENCE lighting_data_id_seq RESTART WITH 4;
ALTER SEQUENCE crime_data_id_seq RESTART WITH 3;
