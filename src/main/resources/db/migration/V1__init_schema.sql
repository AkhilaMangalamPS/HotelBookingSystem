CREATE TABLE hotels (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        rating INT NOT NULL,
                        total_rooms INT NOT NULL,
                        special_note VARCHAR(255) DEFAULT 'None',
                        PRIMARY KEY (id),
                        UNIQUE (name)
);

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       customer_type ENUM('REGULAR','REWARDS') NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('ROLE_ADMIN','ROLE_USER') NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE (username)
);

CREATE TABLE rates (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       customer_type ENUM('REGULAR','REWARDS') NOT NULL,
                       weekday_rate DOUBLE NOT NULL,
                       weekend_rate DOUBLE NOT NULL,
                       hotel_id BIGINT NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE (hotel_id, customer_type),
                       CONSTRAINT fk_rates_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE bookings (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          check_in_date DATE NOT NULL,
                          check_out_date DATE NOT NULL,
                          total_amount DOUBLE NOT NULL,
                          hotel_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_booking_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id),
                          CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id)
);