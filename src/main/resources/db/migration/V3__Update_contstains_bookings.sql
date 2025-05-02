BEGIN;
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_user') THEN
         ALTER TABLE bookings
             DROP CONSTRAINT fk_booking_user;
    END IF;
END $$;

ALTER TABLE bookings
    ADD CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;
COMMIT;
