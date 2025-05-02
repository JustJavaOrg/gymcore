-- Standard SQL syntax that works with both PostgreSQL and H2
ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS fk_booking_user;

ALTER TABLE bookings
    ADD CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;
