import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MainLayout } from '@/layouts';
import { Card, Button, Spinner, Alert } from '@/components';
import { paymentsApi } from '@/api';
import type { InvoiceResponse } from '@/types';

export function InvoicePage() {
  const { rideId } = useParams<{ rideId: string }>();
  const navigate = useNavigate();
  const [invoice, setInvoice] = useState<InvoiceResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (rideId) {
      paymentsApi.getInvoice(Number(rideId))
        .then(setInvoice)
        .catch(() => setError('Failed to load invoice'))
        .finally(() => setLoading(false));
    }
  }, [rideId]);

  if (loading) {
    return (
      <MainLayout>
        <div className="flex justify-center py-12">
          <Spinner size="lg" />
        </div>
      </MainLayout>
    );
  }

  if (error || !invoice) {
    return (
      <MainLayout>
        <Alert variant="error">{error || 'Invoice not found'}</Alert>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">Invoice</h1>
          <Button variant="secondary" onClick={() => window.print()}>
            Print
          </Button>
        </div>

        <Card>
          <div className="space-y-6">
            {/* Header */}
            <div className="flex justify-between border-b pb-4">
              <div>
                <h2 className="text-xl font-bold text-blue-600">AutoStop</h2>
                <p className="text-sm text-gray-500">Safe rides, every time</p>
              </div>
              <div className="text-right">
                <p className="font-medium">{invoice.invoiceNumber}</p>
                <p className="text-sm text-gray-500">
                  {new Date(invoice.issuedAt).toLocaleDateString()}
                </p>
              </div>
            </div>

            {/* Passenger Info */}
            <div>
              <h3 className="text-sm font-medium text-gray-500 mb-1">Bill To</h3>
              <p className="font-medium">{invoice.passengerName}</p>
              <p className="text-sm text-gray-600">{invoice.passengerEmail}</p>
            </div>

            {/* Ride Details */}
            <div className="bg-gray-50 rounded-lg p-4 space-y-3">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">From:</span>
                <span>{invoice.pickupAddress}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">To:</span>
                <span>{invoice.destinationAddress}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Distance:</span>
                <span>{invoice.distanceKm.toFixed(1)} km</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Driver:</span>
                <span>{invoice.driverName}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Start Time:</span>
                <span>{new Date(invoice.rideStartedAt).toLocaleString()}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">End Time:</span>
                <span>{new Date(invoice.rideCompletedAt).toLocaleString()}</span>
              </div>
            </div>

            {/* Total */}
            <div className="border-t pt-4">
              <div className="flex justify-between items-center">
                <span className="text-lg font-medium">Total Amount</span>
                <span className="text-2xl font-bold text-blue-600">
                  {invoice.amount.toLocaleString()} XAF
                </span>
              </div>
              <p className="text-sm text-gray-500 mt-1">
                Payment Reference: {invoice.paymentReference}
              </p>
            </div>
          </div>
        </Card>

        <Button variant="secondary" onClick={() => navigate('/')} className="w-full">
          Back to Home
        </Button>
      </div>
    </MainLayout>
  );
}
