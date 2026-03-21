import { create } from 'zustand';
import type {
  RideResponse,
  RideStatus,
  SafetyStatus,
  EstimateRequest,
  EstimateResponse,
  CreateRideRequest,
  DriverLocationView,
} from '@/types';
import { ridesApi } from '@/api';

interface BookingState {
  pickupLat: number | null;
  pickupLng: number | null;
  pickupAddress: string;
  destinationLat: number | null;
  destinationLng: number | null;
  destinationAddress: string;
  estimate: EstimateResponse | null;
}

interface RideState {
  currentRide: RideResponse | null;
  booking: BookingState;
  driverLocations: DriverLocationView[];
  isLoading: boolean;
  error: string | null;

  // Booking actions
  setPickup: (lat: number, lng: number, address: string) => void;
  setDestination: (lat: number, lng: number, address: string) => void;
  getEstimate: () => Promise<EstimateResponse | null>;
  createRide: (paymentMethodId: number) => Promise<RideResponse>;
  clearBooking: () => void;

  // Ride actions
  fetchRide: (id: number) => Promise<void>;
  updateRideStatus: (status: RideStatus, safetyStatus: SafetyStatus) => void;
  validateDriver: (id: number) => Promise<void>;
  refuseDriver: (id: number) => Promise<void>;
  addDriverLocation: (location: DriverLocationView) => void;
  clearRide: () => void;
  clearError: () => void;
}

const initialBooking: BookingState = {
  pickupLat: null,
  pickupLng: null,
  pickupAddress: '',
  destinationLat: null,
  destinationLng: null,
  destinationAddress: '',
  estimate: null,
};

export const useRideStore = create<RideState>((set, get) => ({
  currentRide: null,
  booking: initialBooking,
  driverLocations: [],
  isLoading: false,
  error: null,

  setPickup: (lat, lng, address) =>
    set((state) => ({
      booking: { ...state.booking, pickupLat: lat, pickupLng: lng, pickupAddress: address },
    })),

  setDestination: (lat, lng, address) =>
    set((state) => ({
      booking: { ...state.booking, destinationLat: lat, destinationLng: lng, destinationAddress: address },
    })),

  getEstimate: async () => {
    const { booking } = get();
    if (!booking.pickupLat || !booking.pickupLng || !booking.destinationLat || !booking.destinationLng) {
      return null;
    }
    set({ isLoading: true, error: null });
    try {
      const request: EstimateRequest = {
        pickupLat: booking.pickupLat,
        pickupLng: booking.pickupLng,
        destinationLat: booking.destinationLat,
        destinationLng: booking.destinationLng,
      };
      const estimate = await ridesApi.estimate(request);
      set((state) => ({
        booking: { ...state.booking, estimate },
        isLoading: false,
      }));
      return estimate;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to get estimate';
      set({ error: message, isLoading: false });
      return null;
    }
  },

  createRide: async (paymentMethodId) => {
    const { booking } = get();
    if (!booking.pickupLat || !booking.pickupLng || !booking.destinationLat || !booking.destinationLng) {
      throw new Error('Missing location data');
    }
    set({ isLoading: true, error: null });
    try {
      const request: CreateRideRequest = {
        pickupLat: booking.pickupLat,
        pickupLng: booking.pickupLng,
        pickupAddress: booking.pickupAddress,
        destinationLat: booking.destinationLat,
        destinationLng: booking.destinationLng,
        destinationAddress: booking.destinationAddress,
        paymentMethodId,
      };
      const ride = await ridesApi.create(request);
      set({ currentRide: ride, isLoading: false });
      return ride;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create ride';
      set({ error: message, isLoading: false });
      throw err;
    }
  },

  clearBooking: () => set({ booking: initialBooking }),

  fetchRide: async (id) => {
    set({ isLoading: true, error: null });
    try {
      const ride = await ridesApi.getById(id);
      set({ currentRide: ride, isLoading: false });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to fetch ride';
      set({ error: message, isLoading: false });
    }
  },

  updateRideStatus: (status, safetyStatus) =>
    set((state) => ({
      currentRide: state.currentRide
        ? { ...state.currentRide, status, safetyStatus }
        : null,
    })),

  validateDriver: async (id) => {
    set({ isLoading: true, error: null });
    try {
      const result = await ridesApi.validateDriver(id);
      set((state) => ({
        currentRide: state.currentRide
          ? { ...state.currentRide, status: result.status, safetyStatus: result.safetyStatus }
          : null,
        isLoading: false,
      }));
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to validate driver';
      set({ error: message, isLoading: false });
      throw err;
    }
  },

  refuseDriver: async (id) => {
    set({ isLoading: true, error: null });
    try {
      const result = await ridesApi.refuseDriver(id);
      set((state) => ({
        currentRide: state.currentRide
          ? { ...state.currentRide, status: result.status, safetyStatus: result.safetyStatus }
          : null,
        isLoading: false,
      }));
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to refuse driver';
      set({ error: message, isLoading: false });
      throw err;
    }
  },

  addDriverLocation: (location) =>
    set((state) => ({
      driverLocations: [...state.driverLocations, location],
    })),

  clearRide: () => set({ currentRide: null, driverLocations: [] }),

  clearError: () => set({ error: null }),
}));
