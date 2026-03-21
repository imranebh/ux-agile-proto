ALTER TABLE drivers
    ADD COLUMN photo_url VARCHAR(512);

ALTER TABLE drivers
    ADD COLUMN rating DECIMAL(3,2);

ALTER TABLE rides
    ADD COLUMN final_price DECIMAL(10,2),
    ADD COLUMN completed_at TIMESTAMP NULL;

ALTER TABLE payment_methods
    ADD COLUMN provider VARCHAR(64) NOT NULL DEFAULT 'MOCK_STRIPE';

ALTER TABLE payment_methods
    ADD COLUMN expiry_month VARCHAR(2) NOT NULL DEFAULT '12';

ALTER TABLE payment_methods
    ADD COLUMN expiry_year VARCHAR(4) NOT NULL DEFAULT '30';

UPDATE rides
SET final_price = estimated_price
WHERE final_price IS NULL;

UPDATE drivers
SET photo_url = 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80',
    rating   = 4.80
WHERE id = 1;
