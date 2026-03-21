# AutoStop Backend MVP

Spring Boot backend MVP for AutoStop with MySQL, JWT auth, Flyway migrations, Swagger, SSE realtime updates, safety-gate ride state machine, mock integrations, payments, invoices, seed data, simulator, and tests.

## Tech Stack
- Java 21, Spring Boot 3
- Spring Web, Data JPA, Security, Validation
- Flyway + MySQL (H2 for tests)
- JWT (`jjwt`)
- Swagger/OpenAPI (`springdoc`)

## Run Locally
1. Copy envs:
   ```bash
   cp .env.example .env
   ```
2. Start MySQL (recommended):
   ```bash
   docker compose up -d mysql
   ```
3. Run API in dev profile:
   ```bash
   SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
   ```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Test
```bash
./mvnw test
```

## Environment Variables
- `DB_URL` (default `jdbc:mysql://localhost:3306/autostop`)
- `DB_USERNAME` (default `autostop`)
- `DB_PASSWORD` (default `autostop`)
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`
- `SERVER_PORT`

## Demo Seed Data (dev)
- Passenger (verified): `passenger@autostop.dev` / `password123`
- Passenger (non-verified): `newbie@autostop.dev` / `password123`
- Driver: `driver@autostop.dev` / `password123`
- One active approaching ride + one vehicle + one driver + one default payment method.
- Dev simulator moves driver every 5s and auto-triggers safety gate at <=50m.

## API Summary
- **Auth**: `/api/auth/register`, `/api/auth/login`, `/api/auth/google`, `/api/auth/me`
- **Users**: `/api/users/me`, `/api/users/me/verify-cni`, `/api/users/me/emergency-contact`
- **Booking**: `/api/locations/autocomplete`, `/api/rides/estimate`, `/api/rides`, `/api/rides/{id}`, `/api/rides/{id}/status`
- **Tracking/Safety**: `/api/rides/{id}/tracking`, `/api/rides/{id}/driver-location`, `/api/rides/{id}/trigger-safety-check`, `/api/rides/{id}/validate-driver`, `/api/rides/{id}/refuse-driver`, `/api/rides/{id}/start`, `/api/rides/{id}/arrive`, `/api/rides/{id}/complete`
- **Payments**: `/api/payments/methods`, `/api/payments/charge-ride/{rideId}`
- **SOS/Invoice**: `/api/sos`, `/api/invoices/{rideId}`
- **Realtime SSE**: `/api/realtime/rides/{rideId}/stream`

## Example cURL
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@autostop.dev","password":"password123","fullName":"Test User","phone":"+237600000010"}'

# Estimate (replace TOKEN)
curl -X POST http://localhost:8080/api/rides/estimate \
  -H "Authorization: Bearer TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"pickupLat":3.84,"pickupLng":11.50,"destinationLat":3.90,"destinationLng":11.55}'

# SSE tracking
curl -N -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/api/realtime/rides/1/stream
```
