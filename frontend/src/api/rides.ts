import api from './client';
import type {
  EstimateRequest,
  EstimateResponse,
  CreateRideRequest,
  RideResponse,
  RideStatusResponse,
  TrackingResponse,
} from '@/types';

export const ridesApi = {
  estimate: (data: EstimateRequest) =>
    api.post<EstimateResponse>('/rides/estimate', data).then((res) => res.data),

  create: (data: CreateRideRequest) =>
    api.post<RideResponse>('/rides', data).then((res) => res.data),

  getById: (id: number) =>
    api.get<RideResponse>(`/rides/${id}`).then((res) => res.data),

  getStatus: (id: number) =>
    api.get<RideStatusResponse>(`/rides/${id}/status`).then((res) => res.data),

  getTracking: (id: number) =>
    api.get<TrackingResponse>(`/rides/${id}/tracking`).then((res) => res.data),

  triggerSafetyCheck: (id: number) =>
    api.post<RideStatusResponse>(`/rides/${id}/trigger-safety-check`).then((res) => res.data),

  validateDriver: (id: number) =>
    api.post<RideStatusResponse>(`/rides/${id}/validate-driver`).then((res) => res.data),

  refuseDriver: (id: number) =>
    api.post<RideStatusResponse>(`/rides/${id}/refuse-driver`).then((res) => res.data),

  cancel: (id: number) =>
    api.delete<void>(`/rides/${id}`),
};
