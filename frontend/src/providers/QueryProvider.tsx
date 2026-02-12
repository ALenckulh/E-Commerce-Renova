// QueryProvider Component
// Provider para TanStack Query (React Query)
// Configurar cache, retry policy, stale time
// TODO: Envolver app com QueryClientProvider

'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactNode } from 'react';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // TODO: Configurar staleTime, gcTime, retry
    },
  },
});

export const QueryProvider = ({ children }: { children: ReactNode }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
);
