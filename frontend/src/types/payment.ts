export type PaymentStatus = 'PENDING' | 'SUCCEEDED' | 'FAILED';

export interface AddPaymentMethodRequest {
  provider: string;
  brand: string;
  cardNumber: string;
  expMonth: string;
  expYear: string;
  cvv: string;
}

export interface PaymentMethodResponse {
  id: number;
  provider: string;
  brand: string;
  last4: string;
  expiryMonth: string;
  expiryYear: string;
  defaultMethod: boolean;
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
