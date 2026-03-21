# Copilot Instructions for `uxagile`

## Build, test, and run commands

- Run API locally (dev profile): `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`
- Start local MySQL (from repo root): `docker compose up -d mysql`
- Build artifact: `./mvnw clean package`
- Run full test suite: `./mvnw test`
- Run a single test class: `./mvnw -Dtest=PricingServiceTest test`
- Run a single test method: `./mvnw -Dtest=PricingServiceTest#shouldCalculatePriceWithBaseDistanceAndTime test`
- Run integration tests only: `./mvnw -Dtest=*IntegrationTest test`
- Swagger UI (dev): `http://localhost:8080/swagger-ui.html`

## High-level architecture

- This is a Spring Boot 3 / Java 21 backend organized as controller -> service -> repository -> JPA entity layers.
- Authentication is stateless JWT:
  - `SecurityConfig` wires a `JwtAuthenticationFilter`, permits only auth + Swagger endpoints anonymously, and protects everything else.
  - Controllers read identity through `SecurityUtils.currentUserEmail()` and pass email to services.
- The core domain is a ride lifecycle with a safety gate:
  - `RideService` owns status transitions (`DRIVER_EN_ROUTE` -> safety checks -> approval/refusal -> start/arrive/complete).
  - Entering key states publishes SSE events via `RealtimeTrackingService` (`SseRealtimeTrackingService` implementation).
  - Driver tracking events also control safety behavior (auto-trigger when driver is within 50m).
- Payment/invoice flow is decoupled but chained:
  - `PaymentService` allows charging only completed rides, reuses existing successful payment when present, and generates an invoice on successful charge through `InvoiceService`.
- External dependencies are abstracted behind provider interfaces (`MapsProvider`, `PaymentProvider`, `GoogleAuthProvider`, `IdentityVerificationProvider`) with mock implementations currently in use.
- Persistence uses Flyway SQL migrations (`db/migration`) with `spring.jpa.hibernate.ddl-auto=validate`; schema changes should be migration-first.
- Runtime behavior varies by profile:
  - `dev`: seeded demo data + scheduled driver simulator (`autostop.demo.simulator-enabled=true`).
  - `test`: H2 in MySQL mode, Flyway enabled, simulator disabled.

## Key conventions in this repository

- DTOs are grouped by feature into single wrapper classes (`AuthDtos`, `RideDtos`, `PaymentDtos`, etc.) with nested static request/response types; follow this pattern rather than creating scattered DTO files.
- Mapping logic is explicit in mapper components (`RideMapper`, `PaymentMapper`, `UserMapper`), not in controllers.
- Business rule failures should throw `ApiException` with an `HttpStatus`; `GlobalExceptionHandler` formats API error payloads consistently.
- Access control is enforced in services via ownership checks (for passenger/driver and ride/payment ownership), not just at the controller layer.
- Ride state transitions must keep status/safety status/updated timestamp aligned and should emit realtime updates (see `RideService.transitionTo`).
- Sensitive location data convention: passenger pickup coordinates stay hidden until `passengerLocationUnlocked` is set after driver validation.
- Tests commonly use `@SpringBootTest`, `@ActiveProfiles("test")`, and `@DirtiesContext(BEFORE_EACH_TEST_METHOD)` to isolate stateful flow scenarios.
- Use `jakarta.persistence` and `jakarta.validation` (not javax) for all JPA and validation imports.
- Financial values use `BigDecimal` (precision 10, scale 2); timestamps use `java.time.Instant`.
- Controllers should always use `SecurityUtils.currentUserEmail()` to get the authenticated user and pass it to services.
