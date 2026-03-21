INSERT INTO users (id, email, password_hash, full_name, phone, role, verification_status, cni_masked, emergency_contact, google_id, created_at)
VALUES
(1, 'passenger@autostop.dev', '$2b$12$1C6.9y.1wVzJEVvMva2./OXBNA3W/OAi8TQM8CJMni2dJWJPs/NSa', 'Alice Passenger', '+237600000001', 'PASSENGER', 'VERIFIED', '******45', '+237699999999', NULL, CURRENT_TIMESTAMP),
(2, 'newbie@autostop.dev', '$2b$12$1C6.9y.1wVzJEVvMva2./OXBNA3W/OAi8TQM8CJMni2dJWJPs/NSa', 'Bob Newbie', '+237600000002', 'PASSENGER', 'NOT_VERIFIED', NULL, NULL, NULL, CURRENT_TIMESTAMP),
(3, 'driver@autostop.dev', '$2b$12$1C6.9y.1wVzJEVvMva2./OXBNA3W/OAi8TQM8CJMni2dJWJPs/NSa', 'Chris Driver', '+237600000003', 'DRIVER', 'VERIFIED', NULL, NULL, NULL, CURRENT_TIMESTAMP);

INSERT INTO passenger_profiles (id, user_id, preferences) VALUES (1, 1, 'quiet ride');

INSERT INTO drivers (id, user_id, status, current_lat, current_lng) VALUES (1, 3, 'ON_TRIP', 3.8500, 11.5000);

INSERT INTO vehicles (id, driver_id, plate_number, model, color) VALUES (1, 1, 'LT-123-AA', 'Toyota Yaris', 'White');

INSERT INTO rides (id, passenger_id, driver_id, vehicle_id, pickup_address, destination_address, pickup_lat, pickup_lng, destination_lat, destination_lng,
                   passenger_location_unlocked, status, safety_status, estimated_price, distance_km, duration_minutes, requested_at, updated_at)
VALUES (1, 1, 1, 1, 'Mvog-Ada, Yaounde', 'Bastos, Yaounde', 3.8480, 11.5010, 3.8800, 11.5200,
        FALSE, 'DRIVER_EN_ROUTE', 'NORMAL', 15.30, 6.20, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO payment_methods (id, user_id, brand, last4, token, default_method, created_at)
VALUES (1, 1, 'VISA', '4242', 'tok_4242', TRUE, CURRENT_TIMESTAMP);
