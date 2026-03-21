import api from './client';
import type { UserResponse } from '@/types';

export interface UpdateProfileRequest {
  fullName: string;
  phone: string;
}

export interface VerifyCniRequest {
  cniNumber: string;
}

export interface EmergencyContactRequest {
  name: string;
  phone: string;
}

export const usersApi = {
  getProfile: () =>
    api.get<UserResponse>('/users/me').then((res) => res.data),

  updateProfile: (data: UpdateProfileRequest) =>
    api.put<UserResponse>('/users/me', data).then((res) => res.data),

  verifyCni: (data: VerifyCniRequest) =>
    api.post<UserResponse>('/users/me/verify-cni', data).then((res) => res.data),

  updateEmergencyContact: (data: EmergencyContactRequest) =>
    api.put<UserResponse>('/users/me/emergency-contact', data).then((res) => res.data),
};
