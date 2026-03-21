import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix for default marker icons in Leaflet with bundlers
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

// @ts-expect-error Leaflet icon fix
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

interface MapProps {
  center?: [number, number];
  zoom?: number;
  className?: string;
  onMapClick?: (lat: number, lng: number) => void;
  markers?: Array<{
    id: string;
    position: [number, number];
    popup?: string;
    color?: 'blue' | 'green' | 'red';
  }>;
  driverPath?: Array<[number, number]>;
}

export function Map({
  center = [3.848, 11.502], // Yaoundé, Cameroon default
  zoom = 13,
  className = '',
  onMapClick,
  markers = [],
  driverPath = [],
}: MapProps) {
  const mapRef = useRef<L.Map | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const markersRef = useRef<L.Marker[]>([]);
  const pathRef = useRef<L.Polyline | null>(null);

  // Initialize map
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current).setView(center, zoom);
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(map);

    if (onMapClick) {
      map.on('click', (e) => {
        onMapClick(e.latlng.lat, e.latlng.lng);
      });
    }

    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
    };
  }, []);

  // Update markers
  useEffect(() => {
    if (!mapRef.current) return;

    // Clear existing markers
    markersRef.current.forEach((m) => m.remove());
    markersRef.current = [];

    // Add new markers
    markers.forEach((m) => {
      const iconColor = m.color || 'blue';
      const icon = new L.Icon({
        iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${iconColor}.png`,
        shadowUrl: markerShadow,
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
      });

      const marker = L.marker(m.position, { icon }).addTo(mapRef.current!);
      if (m.popup) {
        marker.bindPopup(m.popup);
      }
      markersRef.current.push(marker);
    });

    // Fit bounds if multiple markers
    if (markers.length > 1) {
      const bounds = L.latLngBounds(markers.map((m) => m.position));
      mapRef.current.fitBounds(bounds, { padding: [50, 50] });
    }
  }, [markers]);

  // Update driver path
  useEffect(() => {
    if (!mapRef.current) return;

    if (pathRef.current) {
      pathRef.current.remove();
    }

    if (driverPath.length > 1) {
      pathRef.current = L.polyline(driverPath, {
        color: '#3b82f6',
        weight: 4,
        opacity: 0.7,
      }).addTo(mapRef.current);
    }
  }, [driverPath]);

  return (
    <div
      ref={containerRef}
      className={`w-full h-64 rounded-lg overflow-hidden ${className}`}
    />
  );
}
