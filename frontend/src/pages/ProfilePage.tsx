import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { MainLayout } from '@/layouts';
import { Card, Button, Input, Alert } from '@/components';
import { useAuthStore } from '@/stores';
import { usersApi } from '@/api';

const profileSchema = z.object({
  fullName: z.string().min(2, 'Name must be at least 2 characters'),
  phone: z.string().min(10, 'Phone must be at least 10 characters'),
});

const cniSchema = z.object({
  cniNumber: z.string().min(8, 'CNI number must be at least 8 characters'),
});

const emergencySchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  phone: z.string().min(10, 'Phone must be at least 10 characters'),
});

type ProfileFormData = z.infer<typeof profileSchema>;
type CniFormData = z.infer<typeof cniSchema>;
type EmergencyFormData = z.infer<typeof emergencySchema>;

export function ProfilePage() {
  const { user, fetchUser } = useAuthStore();
  const [activeSection, setActiveSection] = useState<'profile' | 'cni' | 'emergency' | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // Profile form
  const profileForm = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      fullName: user?.fullName || '',
      phone: user?.phone || '',
    },
  });

  // CNI form
  const cniForm = useForm<CniFormData>({
    resolver: zodResolver(cniSchema),
  });

  // Emergency contact form
  const emergencyForm = useForm<EmergencyFormData>({
    resolver: zodResolver(emergencySchema),
    defaultValues: {
      name: user?.emergencyContactName || '',
      phone: user?.emergencyContactPhone || '',
    },
  });

  const handleProfileSubmit = async (data: ProfileFormData) => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      await usersApi.updateProfile(data);
      await fetchUser();
      setSuccess('Profile updated successfully');
      setActiveSection(null);
    } catch {
      setError('Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleCniSubmit = async (data: CniFormData) => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      await usersApi.verifyCni(data);
      await fetchUser();
      setSuccess('CNI verification submitted successfully');
      setActiveSection(null);
    } catch {
      setError('Failed to submit CNI verification');
    } finally {
      setLoading(false);
    }
  };

  const handleEmergencySubmit = async (data: EmergencyFormData) => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      await usersApi.updateEmergencyContact(data);
      await fetchUser();
      setSuccess('Emergency contact updated successfully');
      setActiveSection(null);
    } catch {
      setError('Failed to update emergency contact');
    } finally {
      setLoading(false);
    }
  };

  const verificationBadge = {
    PENDING: { text: 'Pending', class: 'bg-yellow-100 text-yellow-800' },
    VERIFIED: { text: 'Verified', class: 'bg-green-100 text-green-800' },
    REJECTED: { text: 'Rejected', class: 'bg-red-100 text-red-800' },
  };

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        <h1 className="text-2xl font-bold text-gray-900">Profile</h1>

        {success && (
          <Alert variant="success" onClose={() => setSuccess(null)}>
            {success}
          </Alert>
        )}
        {error && (
          <Alert variant="error" onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {/* Account Info */}
        <Card>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold">Account Information</h2>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setActiveSection(activeSection === 'profile' ? null : 'profile')}
            >
              {activeSection === 'profile' ? 'Cancel' : 'Edit'}
            </Button>
          </div>

          {activeSection === 'profile' ? (
            <form onSubmit={profileForm.handleSubmit(handleProfileSubmit)} className="space-y-4">
              <Input
                label="Full Name"
                {...profileForm.register('fullName')}
                error={profileForm.formState.errors.fullName?.message}
              />
              <Input
                label="Phone"
                {...profileForm.register('phone')}
                error={profileForm.formState.errors.phone?.message}
              />
              <Button type="submit" isLoading={loading}>
                Save Changes
              </Button>
            </form>
          ) : (
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Name</span>
                <span className="font-medium">{user?.fullName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Email</span>
                <span className="font-medium">{user?.email}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Phone</span>
                <span className="font-medium">{user?.phone}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Role</span>
                <span className="font-medium capitalize">{user?.role?.toLowerCase()}</span>
              </div>
            </div>
          )}
        </Card>

        {/* Identity Verification */}
        <Card>
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <h2 className="text-lg font-semibold">Identity Verification</h2>
              {user?.verificationStatus && (
                <span
                  className={`text-xs px-2 py-1 rounded ${
                    verificationBadge[user.verificationStatus].class
                  }`}
                >
                  {verificationBadge[user.verificationStatus].text}
                </span>
              )}
            </div>
            {user?.verificationStatus !== 'VERIFIED' && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setActiveSection(activeSection === 'cni' ? null : 'cni')}
              >
                {activeSection === 'cni' ? 'Cancel' : 'Verify'}
              </Button>
            )}
          </div>

          {activeSection === 'cni' ? (
            <form onSubmit={cniForm.handleSubmit(handleCniSubmit)} className="space-y-4">
              <p className="text-sm text-gray-600">
                Enter your National ID (CNI) number to verify your identity.
              </p>
              <Input
                label="CNI Number"
                {...cniForm.register('cniNumber')}
                error={cniForm.formState.errors.cniNumber?.message}
                placeholder="Enter your CNI number"
              />
              <Button type="submit" isLoading={loading}>
                Submit for Verification
              </Button>
            </form>
          ) : (
            <div className="space-y-2">
              {user?.cniMasked ? (
                <div className="flex justify-between">
                  <span className="text-gray-600">CNI</span>
                  <span className="font-mono">{user.cniMasked}</span>
                </div>
              ) : (
                <p className="text-gray-500 text-sm">
                  Verify your identity to unlock full features.
                </p>
              )}
            </div>
          )}
        </Card>

        {/* Emergency Contact */}
        <Card>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold">Emergency Contact</h2>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setActiveSection(activeSection === 'emergency' ? null : 'emergency')}
            >
              {activeSection === 'emergency' ? 'Cancel' : user?.emergencyContactName ? 'Edit' : 'Add'}
            </Button>
          </div>

          {activeSection === 'emergency' ? (
            <form onSubmit={emergencyForm.handleSubmit(handleEmergencySubmit)} className="space-y-4">
              <Input
                label="Contact Name"
                {...emergencyForm.register('name')}
                error={emergencyForm.formState.errors.name?.message}
                placeholder="Emergency contact name"
              />
              <Input
                label="Contact Phone"
                {...emergencyForm.register('phone')}
                error={emergencyForm.formState.errors.phone?.message}
                placeholder="+237600000000"
              />
              <Button type="submit" isLoading={loading}>
                Save Contact
              </Button>
            </form>
          ) : user?.emergencyContactName ? (
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Name</span>
                <span className="font-medium">{user.emergencyContactName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Phone</span>
                <span className="font-medium">{user.emergencyContactPhone}</span>
              </div>
            </div>
          ) : (
            <p className="text-gray-500 text-sm">
              Add an emergency contact to be notified in case of an emergency.
            </p>
          )}
        </Card>
      </div>
    </MainLayout>
  );
}
