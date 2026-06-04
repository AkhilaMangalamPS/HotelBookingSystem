--Insert Hotels
INSERT INTO hotels (id, name, rating, total_rooms) VALUES (1, 'Lakewood', 3, 5) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO hotels (id, name, rating, total_rooms) VALUES (2, 'Bridgewood', 4, 5) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO hotels (id, name, rating, total_rooms) VALUES (3, 'Ridgewood', 5, 5) ON DUPLICATE KEY UPDATE name=name;

--Insert Pricing Rates for Lakewood
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (1, 'REGULAR', 110, 90) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (1, 'REWARDS', 80, 80) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;

--Insert Pricing Rates for Bridgewood
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (2, 'REGULAR', 160, 60) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (2, 'REWARDS', 50, 110) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;

--Insert Pricing Rates for Ridgewood
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (3, 'REGULAR', 220, 150) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;
INSERT INTO rates (hotel_id, customer_type, weekday_rate, weekend_rate) VALUES (3, 'REWARDS', 100, 40) ON DUPLICATE KEY UPDATE weekday_rate= weekday_rate;
