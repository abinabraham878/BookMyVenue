Goal

Build an MVP venue booking platform for Phase 1 of BookMyVenue.

Tech Stack

Frontend: Angular
Backend: Spring Boot
Database: PostgreSQL
Payments: Razorpay Sandbox

Architecture

Modular Monolith

Core Modules

Auth
Venue
Search
Booking
Payment

Key Design Decisions

- One venue supports multiple event types
- Cooldown configurable per event type
- Reservation before payment
- Booking snapshot preserves historical rules
- Unit tests mandatory

MVP Scope

- Auth
- Venue creation
- Search
- Availability
- Booking
- Payment