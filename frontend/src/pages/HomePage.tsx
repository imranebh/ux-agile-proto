import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MainLayout } from '@/layouts';
import { Card, Button, Input, Alert, Map, Spinner } from '@/components';
import { useRideStore } from '@/stores';
import { paymentsApi } from '@/api';
import type { PaymentMethodResponse } from '@/types';

export function HomePage() {
  const navigate = useNavigate();
  const {
    booking,
    setPickup,
    setDestination,
    getEstimate,
    createRide,
    clearBooking,
    isLoading,
    error,
    clearError,
  } = useRideStore();

  const [step, setStep] = useState<'pickup' | 'destination' | 'confirm'>('pickup');
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethodResponse[]>([]);
  const [selectedPaymentId, setSelectedPaymentId] = useState<number | null>(null);
  const [loadingMethods, setLoadingMethods] = useState(false);
  const [geoLoading, setGeoLoading] = useState(false);
  const [geoError, setGeoError] = useState<string | null>(null);

  const handleUseMyLocation = () => {
    if (!navigator.geolocation) {
      setGeoError('Geolocation is not supported by your browser');
      return;
    }

    setGeoLoading(true);
    setGeoError(null);

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setPickup(latitude, longitude, `${latitude.toFixed(4)}, ${longitude.toFixed(4)}`);
        setGeoLoading(false);
      },
      (err) => {
        setGeoLoading(false);
        switch (err.code) {
          case err.PERMISSION_DENIED:
            setGeoError('Location access denied. Please enable location permissions.');
            break;
          case err.POSITION_UNAVAILABLE:
            setGeoError('Location information unavailable.');
            break;
          case err.TIMEOUT:
            setGeoError('Location request timed out.');
            break;
          default:
            setGeoError('Unable to get your location.');
        }
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 }
    );
  };

  // Load payment methods when reaching confirm step
  useEffect(() => {
    if (step === 'confirm') {
      setLoadingMethods(true);
      paymentsApi.getMethods()
        .then((methods) => {
          setPaymentMethods(methods);
          const defaultMethod = methods.find((m) => m.defaultMethod);
          if (defaultMethod) setSelectedPaymentId(defaultMethod.id);
          else if (methods.length > 0) setSelectedPaymentId(methods[0].id);
        })
        .catch(console.error)
        .finally(() => setLoadingMethods(false));
    }
  }, [step]);

  const handleMapClick = (lat: number, lng: number) => {
    if (step === 'pickup') {
      setPickup(lat, lng, `${lat.toFixed(4)}, ${lng.toFixed(4)}`);
    } else if (step === 'destination') {
      setDestination(lat, lng, `${lat.toFixed(4)}, ${lng.toFixed(4)}`);
    }
  };

  const handleNext = async () => {
    if (step === 'pickup' && booking.pickupLat) {
      setStep('destination');
    } else if (step === 'destination' && booking.destinationLat) {
      await getEstimate();
      setStep('confirm');
    }
  };

  const handleBack = () => {
    if (step === 'destination') setStep('pickup');
    else if (step === 'confirm') setStep('destination');
  };

  const handleBookRide = async () => {
    if (!selectedPaymentId) return;
    try {
      const ride = await createRide(selectedPaymentId);
      clearBooking();
      navigate(`/rides/${ride.id}/tracking`);
    } catch {
      // Error handled by store
    }
  };

  const markers = [];
  if (booking.pickupLat && booking.pickupLng) {
    markers.push({
      id: 'pickup',
      position: [booking.pickupLat, booking.pickupLng] as [number, number],
      popup: 'Pickup Location',
      color: 'green' as const,
    });
  }
  if (booking.destinationLat && booking.destinationLng) {
    markers.push({
      id: 'destination',
      position: [booking.destinationLat, booking.destinationLng] as [number, number],
      popup: 'Destination',
      color: 'red' as const,
    });
  }

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        <h1 className="text-2xl font-bold text-gray-900">Book a Ride</h1>

        {error && (
          <Alert variant="error" onClose={clearError}>
            {error}
          </Alert>
        )}

        {/* Progress indicator */}
        <div className="flex items-center justify-between mb-4">
          {['pickup', 'destination', 'confirm'].map((s, i) => (
            <div key={s} className="flex items-center">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                  step === s
                    ? 'bg-blue-600 text-white'
                    : i < ['pickup', 'destination', 'confirm'].indexOf(step)
                    ? 'bg-green-500 text-white'
                    : 'bg-gray-200 text-gray-600'
                }`}
              >
                {i + 1}
              </div>
              {i < 2 && <div className="w-16 h-0.5 bg-gray-200 mx-2" />}
            </div>
          ))}
        </div>

        <Card>
          {step === 'pickup' && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold">Select Pickup Location</h2>
              <p className="text-gray-600 text-sm">Click on the map or use your current location</p>
              
              <Button
                variant="secondary"
                onClick={handleUseMyLocation}
                disabled={geoLoading}
                isLoading={geoLoading}
                className="w-full"
              >
                📍 Use My Current Location
              </Button>

              {geoError && (
                <Alert variant="error" onClose={() => setGeoError(null)}>
                  {geoError}
                </Alert>
              )}

              <Map
                className="h-72"
                onMapClick={handleMapClick}
                markers={markers}
                center={booking.pickupLat && booking.pickupLng ? [booking.pickupLat, booking.pickupLng] : undefined}
              />
              <Input
                label="Pickup Address"
                value={booking.pickupAddress}
                onChange={(e) => setPickup(booking.pickupLat || 0, booking.pickupLng || 0, e.target.value)}
                placeholder="Enter address or click map"
              />
              <Button
                onClick={handleNext}
                disabled={!booking.pickupLat}
                className="w-full"
              >
                Continue
              </Button>
            </div>
          )}

          {step === 'destination' && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold">Select Destination</h2>
              <p className="text-gray-600 text-sm">Click on the map to set your destination</p>
              <Map
                className="h-72"
                onMapClick={handleMapClick}
                markers={markers}
              />
              <Input
                label="Destination Address"
                value={booking.destinationAddress}
                onChange={(e) => setDestination(booking.destinationLat || 0, booking.destinationLng || 0, e.target.value)}
                placeholder="Enter address or click map"
              />
              <div className="flex gap-3">
                <Button variant="secondary" onClick={handleBack} className="flex-1">
                  Back
                </Button>
                <Button
                  onClick={handleNext}
                  disabled={!booking.destinationLat || isLoading}
                  isLoading={isLoading}
                  className="flex-1"
                >
                  Get Estimate
                </Button>
              </div>
            </div>
          )}

          {step === 'confirm' && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold">Confirm Your Ride</h2>
              
              <Map className="h-48" markers={markers} />

              <div className="bg-gray-50 rounded-lg p-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">From:</span>
                  <span className="font-medium">{booking.pickupAddress}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">To:</span>
                  <span className="font-medium">{booking.destinationAddress}</span>
                </div>
                {booking.estimate && (
                  <>
                    <div className="border-t pt-2 mt-2">
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Distance:</span>
                        <span>{booking.estimate.distanceKm.toFixed(1)} km</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Duration:</span>
                        <span>{booking.estimate.durationMinutes} min</span>
                      </div>
                      <div className="flex justify-between text-lg font-semibold mt-2">
                        <span>Estimated Price:</span>
                        <span className="text-blue-600">
                          {booking.estimate.estimatedPrice.toLocaleString()} XAF
                        </span>
                      </div>
                    </div>
                  </>
                )}
              </div>

              {/* Payment method selection */}
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Payment Method
                </label>
                {loadingMethods ? (
                  <div className="flex justify-center py-4">
                    <Spinner size="sm" />
                  </div>
                ) : paymentMethods.length === 0 ? (
                  <Alert variant="warning">
                    No payment methods found.{' '}
                    <a href="/payments" className="underline">Add one</a>
                  </Alert>
                ) : (
                  <div className="space-y-2">
                    {paymentMethods.map((method) => (
                      <label
                        key={method.id}
                        className={`flex items-center p-3 border rounded-lg cursor-pointer transition-colors ${
                          selectedPaymentId === method.id
                            ? 'border-blue-500 bg-blue-50'
                            : 'border-gray-200 hover:bg-gray-50'
                        }`}
                      >
                        <input
                          type="radio"
                          name="paymentMethod"
                          value={method.id}
                          checked={selectedPaymentId === method.id}
                          onChange={() => setSelectedPaymentId(method.id)}
                          className="mr-3"
                        />
                        <span className="font-mono">{method.brand} •••• {method.last4}</span>
                        {method.defaultMethod && (
                          <span className="ml-2 text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded">
                            Default
                          </span>
                        )}
                      </label>
                    ))}
                  </div>
                )}
              </div>

              <div className="flex gap-3">
                <Button variant="secondary" onClick={handleBack} className="flex-1">
                  Back
                </Button>
                <Button
                  onClick={handleBookRide}
                  disabled={!selectedPaymentId || isLoading}
                  isLoading={isLoading}
                  className="flex-1"
                >
                  Book Ride
                </Button>
              </div>
            </div>
          )}
        </Card>
      </div>
    </MainLayout>
  );
}
