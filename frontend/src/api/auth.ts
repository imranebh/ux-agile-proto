import api from './client';
import type { RegisterRequest, LoginRequest, TokenResponse, UserResponse } from '@/types';

export const authApi = {
  register: (data: RegisterRequest) =>
    api.post<TokenResponse>('/auth/register', data).then((res) => res.data),

  login: (data: LoginRequest) =>
    api.post<TokenResponse>('/auth/login', data).then((res) => res.data),

  me: () =>
    api.get<UserResponse>('/auth/me').then((res) => res.data),
};
