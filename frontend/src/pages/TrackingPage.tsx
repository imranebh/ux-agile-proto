import { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MainLayout } from '@/layouts';
import { Card, Button, Alert, Spinner, Modal, Map } from '@/components';
import { useRideStore } from '@/stores';
import { useSSE } from '@/hooks';
import { ridesApi } from '@/api';
import type { RideStatus, SafetyStatus, DriverLocationView } from '@/types';

const statusLabels: Record<RideStatus, string> = {
  PENDING: 'Finding a driver...',
  DRIVER_EN_ROUTE: 'Driver is on the way',
  AWAITING_PASSENGER_DECISION: 'Safety check required',
  APPROVED_TO_APPROACH: 'Driver approaching pickup',
  DRIVER_REFUSED: 'Driver was refused',
  RIDE_STARTED: 'Ride in progress',
  ARRIVED_AT_DESTINATION: 'Arrived at destination',
  COMPLETED: 'Ride completed',
  CANCELLED: 'Ride cancelled',
};

const statusColors: Record<RideStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  DRIVER_EN_ROUTE: 'bg-blue-100 text-blue-800',
  AWAITING_PASSENGER_DECISION: 'bg-orange-100 text-orange-800',
  APPROVED_TO_APPROACH: 'bg-green-100 text-green-800',
  DRIVER_REFUSED: 'bg-red-100 text-red-800',
  RIDE_STARTED: 'bg-purple-100 text-purple-800',
  ARRIVED_AT_DESTINATION: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-gray-100 text-gray-800',
  CANCELLED: 'bg-red-100 text-red-800',
};

export function TrackingPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const rideId = Number(id);

  const {
    currentRide,
    driverLocations,
    fetchRide,
    updateRideStatus,
    addDriverLocation,
    validateDriver,
    refuseDriver,
    clearRide,
    isLoading,
    error,
    clearError,
  } = useRideStore();

  const [showSafetyModal, setShowSafetyModal] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  // Fetch ride data on mount (clear previous driver locations but not during initial load)
  useEffect(() => {
    if (rideId && !isNaN(rideId)) {
      // Only clear if switching to a different ride
      if (currentRide && currentRide.id !== rideId) {
        clearRide();
      }
      fetchRide(rideId);
    }
    return () => {
      clearRide();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps -- Only re-fetch when rideId changes
  }, [rideId]);

  // SSE message handler
  const handleSSEMessage = useCallback(
    (event: MessageEvent) => {
      try {
        const data = JSON.parse(event.data);
        
        if (data.type === 'LOCATION_UPDATE') {
          const location = data.payload as DriverLocationView;
          addDriverLocation(location);
        } else if (data.type === 'STATUS_CHANGE') {
          const { status, safetyStatus } = data.payload as {
            status: RideStatus;
            safetyStatus: SafetyStatus;
          };
          updateRideStatus(status, safetyStatus);
          
          if (status === 'AWAITING_PASSENGER_DECISION') {
            setShowSafetyModal(true);
          }
        } else if (data.type === 'SAFETY_CHECK_TRIGGERED') {
          setShowSafetyModal(true);
        }
      } catch (e) {
        console.error('Failed to parse SSE message:', e);
      }
    },
    [addDriverLocation, updateRideStatus]
  );

  // Connect to SSE
  const sseUrl = rideId ? `/api/realtime/rides/${rideId}/stream` : null;
  useSSE(sseUrl, {
    onMessage: handleSSEMessage,
    onError: () => console.error('SSE connection error'),
  });

  // Show safety modal when status changes
  useEffect(() => {
    if (currentRide?.status === 'AWAITING_PASSENGER_DECISION') {
      setShowSafetyModal(true);
    }
  }, [currentRide?.status]);

  const handleValidateDriver = async () => {
    setActionLoading(true);
    try {
      await validateDriver(rideId);
      setShowSafetyModal(false);
    } catch {
      // Error handled by store
    } finally {
      setActionLoading(false);
    }
  };

  const handleRefuseDriver = async () => {
    setActionLoading(true);
    try {
      await refuseDriver(rideId);
      setShowSafetyModal(false);
    } catch {
      // Error handled by store
    } finally {
      setActionLoading(false);
    }
  };

  const handleTriggerSOS = async () => {
    try {
      await ridesApi.triggerSafetyCheck(rideId);
      alert('SOS triggered! Emergency contacts notified.');
    } catch {
      alert('Failed to trigger SOS');
    }
  };

  if (isLoading && !currentRide) {
    return (
      <MainLayout>
        <div className="flex justify-center items-center h-64">
          <Spinner size="lg" />
        </div>
      </MainLayout>
    );
  }

  if (!currentRide) {
    return (
      <MainLayout>
        <Alert variant="error">Ride not found</Alert>
      </MainLayout>
    );
  }

  // Build markers for map
  const markers = [];
  if (currentRide.pickupLat && currentRide.pickupLng) {
    markers.push({
      id: 'pickup',
      position: [currentRide.pickupLat, currentRide.pickupLng] as [number, number],
      popup: 'Pickup',
      color: 'green' as const,
    });
  }
  markers.push({
    id: 'destination',
    position: [currentRide.destinationLat, currentRide.destinationLng] as [number, number],
    popup: 'Destination',
    color: 'red' as const,
  });

  // Add latest driver location
  if (driverLocations.length > 0) {
    const latest = driverLocations[driverLocations.length - 1];
    markers.push({
      id: 'driver',
      position: [latest.lat, latest.lng] as [number, number],
      popup: 'Driver',
      color: 'blue' as const,
    });
  }

  const driverPath = driverLocations.map((l) => [l.lat, l.lng] as [number, number]);
  const isActiveRide = !['COMPLETED', 'CANCELLED', 'DRIVER_REFUSED'].includes(currentRide.status);

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">Ride #{currentRide.id}</h1>
          <span
            className={`px-3 py-1 rounded-full text-sm font-medium ${
              statusColors[currentRide.status]
            }`}
          >
            {statusLabels[currentRide.status]}
          </span>
        </div>

        {error && (
          <Alert variant="error" onClose={clearError}>
            {error}
          </Alert>
        )}

        <Card padding="none">
          <Map className="h-72" markers={markers} driverPath={driverPath} />
        </Card>

        {/* Driver info */}
        {currentRide.driver && (
          <Card>
            <h3 className="font-semibold mb-3">Your Driver</h3>
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                <span className="text-xl">🚗</span>
              </div>
              <div className="flex-1">
                <p className="font-medium">{currentRide.driver.fullName}</p>
                <p className="text-sm text-gray-600">{currentRide.driver.phone}</p>
              </div>
              {currentRide.vehicle && (
                <div className="text-right text-sm">
                  <p className="font-medium">
                    {currentRide.vehicle.make} {currentRide.vehicle.model}
                  </p>
                  <p className="text-gray-600">{currentRide.vehicle.licensePlate}</p>
                  <p className="text-gray-500">{currentRide.vehicle.color}</p>
                </div>
              )}
            </div>
          </Card>
        )}

        {/* Ride details */}
        <Card>
          <h3 className="font-semibold mb-3">Ride Details</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">From:</span>
              <span>{currentRide.pickupAddress}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">To:</span>
              <span>{currentRide.destinationAddress}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Estimated Price:</span>
              <span className="font-medium">
                {currentRide.estimatedPrice.toLocaleString()} XAF
              </span>
            </div>
            {currentRide.finalPrice && (
              <div className="flex justify-between">
                <span className="text-gray-600">Final Price:</span>
                <span className="font-semibold text-blue-600">
                  {currentRide.finalPrice.toLocaleString()} XAF
                </span>
              </div>
            )}
          </div>
        </Card>

        {/* Action buttons */}
        <div className="flex gap-3">
          {isActiveRide && (
            <Button variant="danger" onClick={handleTriggerSOS} className="flex-1">
              🚨 SOS
            </Button>
          )}
          {currentRide.status === 'COMPLETED' && (
            <Button onClick={() => navigate(`/rides/${rideId}/invoice`)} className="flex-1">
              View Invoice
            </Button>
          )}
          <Button variant="secondary" onClick={() => navigate('/')} className="flex-1">
            Back to Home
          </Button>
        </div>

        {/* Safety Check Modal */}
        <Modal
          isOpen={showSafetyModal}
          onClose={() => {}}
          title="🛡️ Safety Check Required"
        >
          <div className="space-y-4">
            <p className="text-gray-600">
              Your driver has arrived nearby. Please verify the driver and vehicle
              details before proceeding.
            </p>
            
            {currentRide.driver && currentRide.vehicle && (
              <div className="bg-gray-50 rounded-lg p-4 space-y-2">
                <p>
                  <strong>Driver:</strong> {currentRide.driver.fullName}
                </p>
                <p>
                  <strong>Vehicle:</strong> {currentRide.vehicle.color}{' '}
                  {currentRide.vehicle.make} {currentRide.vehicle.model}
                </p>
                <p>
                  <strong>License Plate:</strong> {currentRide.vehicle.licensePlate}
                </p>
              </div>
            )}

            <p className="text-sm text-gray-500">
              Only confirm if you can see and verify the driver matches these details.
            </p>

            <div className="flex gap-3 pt-2">
              <Button
                variant="danger"
                onClick={handleRefuseDriver}
                isLoading={actionLoading}
                className="flex-1"
              >
                Refuse Driver
              </Button>
              <Button
                onClick={handleValidateDriver}
                isLoading={actionLoading}
                className="flex-1"
              >
                Confirm Driver
              </Button>
            </div>
          </div>
        </Modal>
      </div>
    </MainLayout>
  );
}
