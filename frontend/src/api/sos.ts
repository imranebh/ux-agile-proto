import api from './client';

export interface SosRequest {
  rideId: number;
  message?: string;
}

export interface SosResponse {
  id: number;
  rideId: number;
  message: string;
  triggeredAt: string;
}

export const sosApi = {
  trigger: (data: SosRequest) =>
    api.post<SosResponse>('/sos', data).then((res) => res.data),
};
