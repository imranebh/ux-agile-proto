import { useEffect, useRef, useCallback } from 'react';

interface UseSSEOptions {
  onMessage: (event: MessageEvent) => void;
  onError?: (event: Event) => void;
  onOpen?: () => void;
}

export function useSSE(url: string | null, options: UseSSEOptions) {
  const eventSourceRef = useRef<EventSource | null>(null);

  const connect = useCallback(() => {
    if (!url) return;

    const token = localStorage.getItem('token');
    const fullUrl = token ? `${url}?token=${encodeURIComponent(token)}` : url;
    
    const eventSource = new EventSource(fullUrl);
    eventSourceRef.current = eventSource;

    eventSource.onopen = () => {
      options.onOpen?.();
    };

    eventSource.onmessage = (event) => {
      options.onMessage(event);
    };

    eventSource.onerror = (event) => {
      options.onError?.(event);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [url, options]);

  useEffect(() => {
    const cleanup = connect();
    return () => cleanup?.();
  }, [connect]);

  const close = useCallback(() => {
    eventSourceRef.current?.close();
    eventSourceRef.current = null;
  }, []);

  return { close };
}
