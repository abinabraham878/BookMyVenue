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
- User
- Venue
- Search
- Reservation
- Booking
- Payment

Non-Goals

- Microservices
- Kafka
- Redis
- Kubernetes
- Dynamic Pricing
- Recommendation Engine
- Complex Analytics

Booking Principles

- Availability determined by reservation overlap.
- Cooldown periods affect occupancy calculations.
- Reservation created before payment.
- Payment confirmation finalizes booking.
- Historical bookings remain unchanged after policy updates.

Testing

- Unit tests mandatory for service layer.
- Critical business logic must be tested.
- Booking, availability, pricing and payment flows require tests.

Database Migration

Liquibase

All schema changes are version controlled through Liquibase changelogs.
Direct database modifications are prohibited.