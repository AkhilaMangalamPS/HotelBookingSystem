-- Clean up old test data to prevent primary key conflicts on restart
DELETE FROM hotel_amenities WHERE hotel_id = 1;
DELETE FROM rooms WHERE hotel_id = 1;
DELETE FROM hotels WHERE id = 1;

-- 1. Insert Hotel
INSERT INTO hotels (id, name, description, location, address, rating)
VALUES (1, 'Grand Lumiere Resort', 'A luxury boutique stay in the heart of the city.', 'Paris', '123 Rue de Rivoli', 4.8);

-- 2. Insert Amenities
INSERT INTO hotel_amenities (hotel_id, amenity_name) VALUES (1, 'Free High-Speed Wi-Fi');
INSERT INTO hotel_amenities (hotel_id, amenity_name) VALUES (1, 'Rooftop Swimming Pool');

-- 3. Insert Rooms
INSERT INTO rooms (id, room_number, type, price, max_adults, max_children, available, hotel_id)
VALUES (1, '101-A', 'Deluxe Suite', 150.00, 2, 1, b'1', 1);

INSERT INTO rooms (id, room_number, type, price, max_adults, max_children, available, hotel_id)
VALUES (2, '102-B', 'Executive Penthouse', 350.00, 4, 2, b'1', 1);