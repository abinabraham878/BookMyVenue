# BookMyVenue - Architecture Overview

## Project Goal

Build a Minimum Viable Product (MVP) for the BookMyVenue platform as part of the WeCode Phase 1 challenge.

The MVP enables users to discover venues, check availability, make reservations, complete payments, and receive booking confirmations.

Primary objectives:

* Deliver a functional end-to-end booking platform.
* Demonstrate sound software engineering practices.
* Build a maintainable and extensible foundation for future phases.
* Learn and apply backend engineering, system design, testing, and database design principles.

---

## Technology Stack

### Frontend

* Angular

### Backend

* Spring Boot 3.x
* Java 21

### Database

* PostgreSQL

### Database Migration

* Liquibase

### Payment Gateway

* Razorpay Sandbox

### Testing

* JUnit 5
* Mockito

---

## Architecture

### Architectural Style

Modular Monolith

The system is implemented as a single deployable application while maintaining clear boundaries between business domains.

Benefits:

* Faster development and deployment.
* Simpler debugging and maintenance.
* Easier onboarding and learning.
* Future migration to microservices remains possible if required.

---

## Module Structure

### Auth Module

Responsibilities:

* User registration
* User authentication
* JWT token management
* Login and logout flows

### User Module

Responsibilities:

* User profile management
* Role management
* Account information

### Venue Module

Responsibilities:

* Venue management
* Event types
* Amenities
* Venue policies
* Venue images
* Venue ownership

### Search Module

Responsibilities:

* Venue discovery
* Filtering
* Location-based search
* Availability search

### Reservation Module

Responsibilities:

* Availability validation
* Reservation creation
* Temporary booking locks
* Overlap validation

### Booking Module

Responsibilities:

* Booking confirmation
* Booking history
* Booking snapshots
* Booking lifecycle management

### Payment Module

Responsibilities:

* Razorpay integration
* Payment processing
* Payment verification
* Webhook handling

---

## Key Business Rules

### Venue Rules

* A venue can support multiple event types.
* A venue can provide multiple amenities.
* Amenities may be included or chargeable.
* Venue owners can configure event-specific policies.

### Reservation Rules

* Reservation is created before payment.
* Reservation temporarily locks the selected slot.
* Reservation expiration releases the slot.

### Booking Rules

* Booking is created only after successful payment confirmation.
* Historical bookings remain unchanged after policy updates.
* Booking records preserve the rules applicable at booking time.

### Availability Rules

* Availability is determined by reservation and booking overlap checks.
* Cooldown periods impact slot availability.
* Cooldown periods are configurable per event type.

### Payment Rules

* Payment confirmation finalizes a booking.
* Payment provider webhooks are treated as the source of truth.
* Duplicate payment notifications must be handled safely.

---

## Engineering Principles

* Follow clean package boundaries.
* Keep business logic inside service layer.
* Maintain separation between controllers, services, repositories, and entities.
* All schema changes must be managed through Liquibase.
* Direct database modifications are prohibited.
* Unit testing is mandatory for business-critical logic.
* Prefer simplicity over premature optimization.

---

## Testing Strategy

### Mandatory Unit Tests

* Booking flow
* Availability validation
* Reservation overlap validation
* Cooldown calculations
* Payment processing
* Pricing calculations

### Definition of Done

A feature is considered complete only when:

* Implementation is completed.
* Unit tests are written and passing.
* Code review checklist is satisfied.
* Documentation is updated if required.

---

## Non-Goals (Phase 1)

The following items are intentionally excluded from the MVP:

* Microservices
* Kafka
* Redis
* Kubernetes
* Dynamic pricing
* Recommendation engine
* Advanced analytics
* Notification services
* Event-driven architecture

---

## Future Evolution

The modular monolith architecture should allow future extraction of independent services if required:

* Auth Service
* Venue Service
* Reservation Service
* Booking Service
* Payment Service

This migration is not a Phase 1 objective.
