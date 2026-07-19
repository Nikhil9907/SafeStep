'use client';

import dynamic from 'next/dynamic';
import { useState } from 'react';
import Sidebar from './components/Sidebar';
import ReportForm from './components/ReportForm';
import Chatbot from './components/Chatbot';
import { useMap } from './hooks/useMap';



const Map = dynamic(() => import('./components/Map'), {
  ssr: false,
  loading: () => <p>Loading map...</p>,
});

export default function Home() {
  const {
    map,
    startPoint,
    endPoint,
    route,
    isLoading,
    error,
    reports,
    initializeMap,
    setStart,
    setEnd,
    setRoute,
    addReport,
    clearRoute,
  } = useMap();

  const [reportPosition, setReportPosition] = useState(null);
  const [isReportMode, setIsReportMode] = useState(false);
  const [localReports, setLocalReports] = useState([]);

  const handleFindRoute = async () => {
    try {
      const mockRoute = {
        dangerScore: 4,
        coordinates: [
          [startPoint.lat, startPoint.lng],
          [(startPoint.lat + endPoint.lat) / 2, (startPoint.lng + endPoint.lng) / 2],
          [endPoint.lat, endPoint.lng],
        ]
      };
      setRoute(mockRoute);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRightClick = (latlng) => {
    if (isReportMode) {
      setReportPosition(latlng);
    }
  };

  const handleToggleReportMode = () => {
    setIsReportMode(prev => !prev);
    setReportPosition(null);
  };

  const handleReportClose = () => {
    setReportPosition(null);
  };

  const handleReportSubmit = (reportData) => {
    addReport(reportData);
    setLocalReports(prev => [...prev, reportData]);
    setReportPosition(null);
  };

  return (
    <main style={{ position: 'relative' }}>
      <Sidebar
        startPoint={startPoint}
        endPoint={endPoint}
        route={route}
        isLoading={isLoading}
        isReportMode={isReportMode}
        onFindRoute={handleFindRoute}
        onClear={clearRoute}
        onToggleReportMode={handleToggleReportMode}
      />
      {reportPosition && (
        <ReportForm
          position={reportPosition}
          onClose={handleReportClose}
          onSubmit={handleReportSubmit}
        />
      )}
      <Chatbot route={route} />
      <Map
  onStartSet={setStart}
  onEndSet={setEnd}
  route={route}
  onRightClick={handleRightClick}
  reports={localReports}
/>
    </main>
  );
}