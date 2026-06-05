# Hotel Reservation System

A Springboot application that solves the hotel selection problem using a dynamic pricing engine, and transactional booking management.

---

## Features
- Multi-hotel reservation system (Lakewood, Bridgewood, ridgewood)
- Dynamic pricing (weekday vs weekends)
- Discount system ( for reward user)
- Intelligent hotel selection (chepaest + rating tie-breaker)
- Role based access control (Admin/User)
- Spring security with JWT Authentication

---

## Logic
The system selects hotel based on:

- Date range (weekend/weekday calculation)
- Customer type (Regular/ Reward)
- Tie breaker : higher rating win if costs are equal

---

## Input

- Check In Date
- Check Out Date
- Customer Type (Reward/Regular)
- Eg : 05/06/2024 , 08/06/2024 , Regular

---

## Output

- Best hotel Name
- Total price
- booking Confirmation

---

## How it works

- user logs in
- Selects check in date and check out dates, select customer type
- System calculates weekend/ weekday split
- Checks available rooms in each hotel
- Compute cost for ecah hotel
- Select chepest hotel (tie -> higher rating will win)
- Saves booking in MySQL
- Retuens confirmation

---

~~~
hotel-reservation-system/
├── src/main/java/com/hotel/reservation/
│   ├── config/          # Security, JWT, App configuration
│   ├── controller/      # REST + Web controllers
│   ├── dto/             # Request/Response objects
│   ├── model/           # JPA Entities (User, Hotel, Booking, Rate)
│   ├── repository/      # Data access layer
│   └── service/         # Business logic + pricing engine
│
├── src/main/resources/
│   ├── templates/       # Thymeleaf UI pages
│   ├── application.properties
│   └── data.sql         # Initial seed data
│
└── pom.xml
~~~

