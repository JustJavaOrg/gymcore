-- V2__Insert_test_data.sql

-- Insert Membership Types
INSERT INTO membership_type (name, description, price)
VALUES
    ('Basic', 'Basic membership plan', 29.99),
    ('Premium', 'Premium membership plan', 59.99);

-- Insert Users: Two members, one trainer, one admin
INSERT INTO users (name, email, role, membership_type_id)
VALUES
    ('Alice Smith', 'alice@example.com', 'MEMBER', 1),
    ('Bob Johnson', 'bob@example.com', 'MEMBER', 2),
    ('Charlie Trainer', 'charlie@example.com', 'TRAINER', NULL),
    ('Dana Admin', 'dana@example.com', 'ADMIN', NULL);

-- Insert Classes: Two classes run by the trainer (id = 3)
INSERT INTO classes (title, description, start_time, end_time, capacity, trainer_id)
VALUES
    ('Yoga Class', 'Morning yoga session', '2025-02-05 08:00:00', '2025-02-05 09:00:00', 20, 3),
    ('Pilates Class', 'Pilates session for beginners', '2025-02-05 10:00:00', '2025-02-05 11:00:00', 15, 3);

-- Insert Bookings: Each member books one class
INSERT INTO bookings (user_id, class_id)
VALUES
    (1, 1),
    (2, 2);
