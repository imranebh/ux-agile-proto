export type PaymentStatus = 'PENDING' | 'SUCCEEDED' | 'FAILED';

export interface AddPaymentMethodRequest {
  cardNumber: string;
  expMonth: string;
  expYear: string;
  cvv: string;
  isDefault: boolean;
}

export interface PaymentMethodResponse {
  id: number;
  maskedCard: string;
  isDefault: boolean;
  createdAt: string;
}

export interface PaymentResponse {
  id: number;
  rideId: number;
  amount: number;
  status: PaymentStatus;
  reference: string;
  paidAt: string;
}

export interface InvoiceResponse {
  id: number;
  rideId: number;
  invoiceNumber: string;
  passengerName: string;
  passengerEmail: string;
  driverName: string;
  pickupAddress: string;
  destinationAddress: string;
  distanceKm: number;
  rideStartedAt: string;
  rideCompletedAt: string;
  amount: number;
  paymentReference: string;
  issuedAt: string;
}
