import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { MainLayout } from '@/layouts';
import { Card, Button, Input, Alert, Spinner, Modal } from '@/components';
import { paymentsApi } from '@/api';
import type { PaymentMethodResponse } from '@/types';

const cardSchema = z.object({
  cardNumber: z.string().min(12).max(19).regex(/^\d+$/, 'Invalid card number'),
  expMonth: z.string().length(2).regex(/^(0[1-9]|1[0-2])$/, 'Invalid month'),
  expYear: z.string().min(2).max(4).regex(/^\d{2,4}$/, 'Invalid year'),
  cvv: z.string().min(3).max(4).regex(/^\d+$/, 'Invalid CVV'),
});

type CardFormData = z.infer<typeof cardSchema>;

export function PaymentsPage() {
  const [methods, setMethods] = useState<PaymentMethodResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CardFormData>({
    resolver: zodResolver(cardSchema),
  });

  // Detect card brand from card number
  const detectCardBrand = (cardNumber: string): string => {
    const num = cardNumber.replace(/\s/g, '');
    if (/^4/.test(num)) return 'Visa';
    if (/^5[1-5]/.test(num) || /^2[2-7]/.test(num)) return 'Mastercard';
    if (/^3[47]/.test(num)) return 'Amex';
    if (/^6(?:011|5)/.test(num)) return 'Discover';
    return 'Unknown';
  };

  const loadMethods = async () => {
    try {
      const data = await paymentsApi.getMethods();
      setMethods(data);
    } catch {
      setError('Failed to load payment methods');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMethods();
  }, []);

  const onSubmit = async (data: CardFormData) => {
    setSubmitting(true);
    setError(null);
    try {
      const brand = detectCardBrand(data.cardNumber);
      await paymentsApi.addMethod({
        provider: 'stripe',
        brand,
        cardNumber: data.cardNumber,
        expMonth: data.expMonth,
        expYear: data.expYear,
        cvv: data.cvv,
      });
      await loadMethods();
      setShowModal(false);
      reset();
    } catch (err) {
      const axiosError = err as { response?: { data?: { message?: string } } };
      setError(axiosError.response?.data?.message || 'Failed to add payment method');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this payment method?')) return;
    try {
      await paymentsApi.deleteMethod(id);
      setMethods(methods.filter((m) => m.id !== id));
    } catch {
      setError('Failed to delete payment method');
    }
  };

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">Payment Methods</h1>
          <Button onClick={() => setShowModal(true)}>Add Card</Button>
        </div>

        {error && (
          <Alert variant="error" onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {loading ? (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        ) : methods.length === 0 ? (
          <Card>
            <div className="text-center py-8">
              <p className="text-gray-600 mb-4">No payment methods added yet</p>
              <Button onClick={() => setShowModal(true)}>Add Your First Card</Button>
            </div>
          </Card>
        ) : (
          <div className="space-y-3">
            {methods.map((method) => (
              <Card key={method.id}>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                      💳
                    </div>
                    <div>
                      <p className="font-mono font-medium">
                        {method.brand} •••• {method.last4}
                      </p>
                      <p className="text-sm text-gray-500">
                        Expires {method.expiryMonth}/{method.expiryYear}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    {method.defaultMethod && (
                      <span className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded">
                        Default
                      </span>
                    )}
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleDelete(method.id)}
                    >
                      Delete
                    </Button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}

        {/* Add Card Modal */}
        <Modal
          isOpen={showModal}
          onClose={() => setShowModal(false)}
          title="Add Payment Method"
        >
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Card Number"
              {...register('cardNumber')}
              error={errors.cardNumber?.message}
              placeholder="1234567890123456"
              maxLength={19}
            />
            <div className="grid grid-cols-3 gap-3">
              <Input
                label="Month"
                {...register('expMonth')}
                error={errors.expMonth?.message}
                placeholder="MM"
                maxLength={2}
              />
              <Input
                label="Year"
                {...register('expYear')}
                error={errors.expYear?.message}
                placeholder="YYYY"
                maxLength={4}
              />
              <Input
                label="CVV"
                type="password"
                {...register('cvv')}
                error={errors.cvv?.message}
                placeholder="•••"
                maxLength={4}
              />
            </div>
            <div className="flex gap-3 pt-2">
              <Button
                type="button"
                variant="secondary"
                onClick={() => setShowModal(false)}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button type="submit" isLoading={submitting} className="flex-1">
                Add Card
              </Button>
            </div>
          </form>
        </Modal>
      </div>
    </MainLayout>
  );
}
