'use client';

import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { MAP_CONFIG } from '../constants/config';

export default function Map({ onStartSet, onEndSet, route, onRightClick, reports=[] }) {
  const clickCount = useRef(0);
  const mapRef = useRef(null);
  const onRightClickRef = useRef(onRightClick);

  useEffect(() => {
    // Fix Leaflet marker icons in Next.js
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});
    onRightClickRef.current = onRightClick;
  }, [onRightClick]);

  useEffect(() => {
    const map = L.map('map').setView(
      MAP_CONFIG.defaultCenter,
      MAP_CONFIG.defaultZoom
    );

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      minZoom: MAP_CONFIG.minZoom,
      maxZoom: MAP_CONFIG.maxZoom,
    }).addTo(map);

    map.on('click', (e) => {
      clickCount.current += 1;
      if (clickCount.current === 1) {
        onStartSet(e.latlng);
        L.marker(e.latlng).addTo(map).bindPopup('Start Point').openPopup();
      } else if (clickCount.current === 2) {
        onEndSet(e.latlng);
        L.marker(e.latlng).addTo(map).bindPopup('End Point').openPopup();
      }
    });

    map.on('contextmenu', (e) => {
      onRightClickRef.current(e.latlng);
    });

    mapRef.current = map;

    return () => {
      map.remove();
    };
  }, []);

  useEffect(() => {
    if (!mapRef.current || !route || !route.coordinates) return;
    const dangerColor = route.dangerScore <= 3 ? '#27ae60' :
      route.dangerScore <= 6 ? '#f39c12' : '#e74c3c';
    L.polyline(route.coordinates, {
      color: dangerColor,
      weight: 5,
      opacity: 0.8,
    }).addTo(mapRef.current);
  }, [route]);

  useEffect(() => {
    if (!mapRef.current || reports.length === 0) return;
    const latestReport = reports[reports.length - 1];
    const redIcon = L.divIcon({
      html: '🚨',
      className: '',
      iconSize: [24, 24],
    });
    L.marker([latestReport.lat, latestReport.lng], { icon: redIcon })
      .addTo(mapRef.current)
      .bindPopup(`
        <b>${latestReport.reportType}</b><br/>
        ${latestReport.description}<br/>
        Severity: ${latestReport.severity}/5
      `);
  }, [reports]);

  return (
    <div id="map" style={{ width: '100%', height: '100vh' }} />
  );
}