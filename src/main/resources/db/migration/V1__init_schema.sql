CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(64),
    role VARCHAR(32) NOT NULL,
    verification_status VARCHAR(32) NOT NULL,
    cni_masked VARCHAR(255),
    emergency_contact VARCHAR(255),
    google_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE passenger_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    preferences VARCHAR(255),
    CONSTRAINT fk_passenger_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE drivers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    current_lat DOUBLE,
    current_lng DOUBLE,
    CONSTRAINT fk_driver_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    driver_id BIGINT NOT NULL UNIQUE,
    plate_number VARCHAR(64) NOT NULL,
    model VARCHAR(255) NOT NULL,
    color VARCHAR(64) NOT NULL,
    CONSTRAINT fk_vehicle_driver FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

CREATE TABLE rides (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    passenger_id BIGINT NOT NULL,
    driver_id BIGINT,
    vehicle_id BIGINT,
    pickup_address VARCHAR(255) NOT NULL,
    destination_address VARCHAR(255) NOT NULL,
    pickup_lat DOUBLE NOT NULL,
    pickup_lng DOUBLE NOT NULL,
    destination_lat DOUBLE NOT NULL,
    destination_lng DOUBLE NOT NULL,
    passenger_location_unlocked BOOLEAN NOT NULL,
    status VARCHAR(64) NOT NULL,
    safety_status VARCHAR(64) NOT NULL,
    estimated_price DECIMAL(10,2) NOT NULL,
    distance_km DECIMAL(8,2) NOT NULL,
    duration_minutes INT NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ride_passenger FOREIGN KEY (passenger_id) REFERENCES users(id),
    CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_ride_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

CREATE TABLE driver_locations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ride_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    distance_to_pickup_meters DOUBLE NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_driver_location_ride FOREIGN KEY (ride_id) REFERENCES rides(id),
    CONSTRAINT fk_driver_location_driver FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

CREATE TABLE payment_methods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    brand VARCHAR(32) NOT NULL,
    last4 VARCHAR(4) NOT NULL,
    token VARCHAR(255) NOT NULL,
    default_method BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_payment_method_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ride_id BIGINT NOT NULL UNIQUE,
    payer_id BIGINT NOT NULL,
    payment_method_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_payment_ride FOREIGN KEY (ride_id) REFERENCES rides(id),
    CONSTRAINT fk_payment_payer FOREIGN KEY (payer_id) REFERENCES users(id),
    CONSTRAINT fk_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

CREATE TABLE invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ride_id BIGINT NOT NULL UNIQUE,
    payment_id BIGINT NOT NULL UNIQUE,
    invoice_number VARCHAR(64) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_invoice_ride FOREIGN KEY (ride_id) REFERENCES rides(id),
    CONSTRAINT fk_invoice_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE TABLE sos_incidents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ride_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sos_ride FOREIGN KEY (ride_id) REFERENCES rides(id),
    CONSTRAINT fk_sos_user FOREIGN KEY (user_id) REFERENCES users(id)
);
