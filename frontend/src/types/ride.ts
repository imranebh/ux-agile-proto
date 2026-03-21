export type RideStatus = 
  | 'PENDING'
  | 'DRIVER_EN_ROUTE'
  | 'AWAITING_PASSENGER_DECISION'
  | 'APPROVED_TO_APPROACH'
  | 'DRIVER_REFUSED'
  | 'RIDE_STARTED'
  | 'ARRIVED_AT_DESTINATION'
  | 'COMPLETED'
  | 'CANCELLED';

export type SafetyStatus = 
  | 'NOT_TRIGGERED'
  | 'PENDING_VALIDATION'
  | 'VALIDATED'
  | 'REFUSED';

export interface EstimateRequest {
  pickupLat: number;
  pickupLng: number;
  destinationLat: number;
  destinationLng: number;
}

export interface EstimateResponse {
  distanceKm: number;
  durationMinutes: number;
  estimatedPrice: number;
}

export interface CreateRideRequest {
  pickupLat: number;
  pickupLng: number;
  pickupAddress: string;
  destinationLat: number;
  destinationLng: number;
  destinationAddress: string;
  paymentMethodId: number;
}

export interface DriverProfile {
  id: number;
  fullName: string;
  phone: string;
  photoUrl: string | null;
  rating: number;
}

export interface VehicleInfo {
  id: number;
  make: string;
  model: string;
  licensePlate: string;
  color: string;
}

export interface RideResponse {
  id: number;
  status: RideStatus;
  safetyStatus: SafetyStatus;
  pickupAddress: string;
  destinationAddress: string;
  pickupLat: number | null;
  pickupLng: number | null;
  destinationLat: number;
  destinationLng: number;
  estimatedPrice: number;
  finalPrice: number | null;
  requestedAt: string;
  completedAt: string | null;
  driver: DriverProfile | null;
  vehicle: VehicleInfo | null;
}

export interface RideStatusResponse {
  id: number;
  status: RideStatus;
  safetyStatus: SafetyStatus;
  updatedAt: string;
}

export interface DriverLocationView {
  lat: number;
  lng: number;
  recordedAt: string;
}

export interface TrackingResponse {
  rideId: number;
  status: RideStatus;
  safetyStatus: SafetyStatus;
  pickupLat: number | null;
  pickupLng: number | null;
  destinationLat: number;
  destinationLng: number;
  driverLocations: DriverLocationView[];
}

export interface SSEEvent {
  type: 'LOCATION_UPDATE' | 'STATUS_CHANGE' | 'SAFETY_CHECK_TRIGGERED';
  payload: unknown;
}
