import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores';
import { Spinner } from '@/components';
import { useEffect } from 'react';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const location = useLocation();
  const { isAuthenticated, isLoading, token, fetchUser } = useAuthStore();

  useEffect(() => {
    if (token && !isAuthenticated && !isLoading) {
      fetchUser();
    }
  }, [token, isAuthenticated, isLoading, fetchUser]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}
