-- Clean up old data
DELETE FROM booking WHERE id = 1;
DELETE FROM rooms WHERE id IN (1, 2);

-- 1. Sync room representations with matching global IDs
INSERT INTO rooms (id, room_number, type, price, max_adults, max_children, available)
VALUES (1, '101-A', 'Deluxe Suite', 150.00, 2, 1, b'1');

INSERT INTO rooms (id, room_number, type, price, max_adults, max_children, available)
VALUES (2, '102-B', 'Executive Penthouse', 350.00, 4, 2, b'1');

-- 2. Insert Dummy Booking
INSERT INTO booking (id, room_id, user_email, check_in_date, check_out_date, total_amount, status)
VALUES (1, 1, 'guest@example.com', '2026-08-10', '2026-08-15', 750.00, 'CONFIRMED');