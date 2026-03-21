import api from './client';
import type {
  AddPaymentMethodRequest,
  PaymentMethodResponse,
  PaymentResponse,
  InvoiceResponse,
} from '@/types';

export const paymentsApi = {
  getMethods: () =>
    api.get<PaymentMethodResponse[]>('/payments/methods').then((res) => res.data),

  addMethod: (data: AddPaymentMethodRequest) =>
    api.post<PaymentMethodResponse>('/payments/methods', data).then((res) => res.data),

  deleteMethod: (id: number) =>
    api.delete<void>(`/payments/methods/${id}`),

  chargeRide: (rideId: number) =>
    api.post<PaymentResponse>(`/payments/charge-ride/${rideId}`).then((res) => res.data),

  getInvoice: (rideId: number) =>
    api.get<InvoiceResponse>(`/invoices/${rideId}`).then((res) => res.data),
};
