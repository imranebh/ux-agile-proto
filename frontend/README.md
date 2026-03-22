    # AutoStop Frontend

React + TypeScript frontend for the AutoStop ride-hailing application.

## Tech Stack

- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **Routing**: React Router v6
- **State Management**: Zustand
- **API Client**: Axios
- **Forms**: React Hook Form + Zod
- **Styling**: Tailwind CSS
- **Maps**: Leaflet + React-Leaflet

## Getting Started

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

The dev server runs on `http://localhost:5173` and proxies API requests to `http://localhost:8080`.

## Project Structure

```
src/
├── api/           # Typed API client and endpoints
├── components/    # Reusable UI components
├── hooks/         # Custom React hooks (useSSE, etc.)
├── layouts/       # Page layouts (MainLayout, AuthLayout)
├── pages/         # Route-level page components
├── stores/        # Zustand stores (auth, ride)
├── types/         # TypeScript type definitions
└── utils/         # Helper utilities
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Pages

| Route | Component | Description |
|-------|-----------|-------------|
| `/login` | LoginPage | User login |
| `/register` | RegisterPage | New user registration |
| `/` | HomePage | Ride booking (3-step flow) |
| `/rides/:id/tracking` | TrackingPage | Real-time ride tracking |
| `/rides/:rideId/invoice` | InvoicePage | Invoice display |
| `/payments` | PaymentsPage | Payment method management |
| `/profile` | ProfilePage | User profile & verification |

## Features

- **Authentication**: JWT-based auth with persistent login
- **Ride Booking**: Map-based pickup/destination selection, fare estimation
- **Real-time Tracking**: SSE-powered driver location updates
- **Safety Gate**: Driver verification before ride start
- **Payments**: Credit card management, ride charging
- **Profile**: Identity verification, emergency contacts

## Demo Credentials

Use these accounts from the backend seed data:
- Passenger: `passenger@autostop.dev` / `password123`
- Driver: `driver@autostop.dev` / `password123`
