export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  token: string;
}

export interface UserResponse {
  id: number;
  email: string;
  fullName: string;
  phone: string;
  role: 'PASSENGER' | 'DRIVER';
  verificationStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  cniMasked: string | null;
  emergencyContactName: string | null;
  emergencyContactPhone: string | null;
}
