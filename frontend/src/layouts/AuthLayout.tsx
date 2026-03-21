import type { ReactNode } from 'react';
import { Card } from '@/components';

interface AuthLayoutProps {
  children: ReactNode;
  title: string;
  subtitle?: string;
}

export function AuthLayout({ children, title, subtitle }: AuthLayoutProps) {
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-blue-600">AutoStop</h1>
          <p className="text-gray-600 mt-2">Safe rides, every time</p>
        </div>
        <Card>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">{title}</h2>
          {subtitle && <p className="text-gray-600 mb-6">{subtitle}</p>}
          {children}
        </Card>
      </div>
    </div>
  );
}
