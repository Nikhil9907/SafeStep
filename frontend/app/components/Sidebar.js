'use client';

import { DANGER_LEVELS } from '../constants/config';

export default function Sidebar({ startPoint, endPoint, route, isLoading, onFindRoute, onClear,isReportMode, onToggleReportMode }) {
  const getDangerLevel = (score) => {
    if (score <= 3) return DANGER_LEVELS.LOW;
    if (score <= 6) return DANGER_LEVELS.MEDIUM;
    return DANGER_LEVELS.HIGH;
  };

  return (
    <div style={{
      position: 'absolute',
      top: '20px',
      left: '20px',
      zIndex: 1000,
      background: 'white',
      borderRadius: '16px',
      padding: '1.5rem',
      width: '300px',
      boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
    }}>
      <h1 style={{ fontSize: '22px', fontWeight: 'bold', marginBottom: '1.5rem', color: '#1a1a1a' }}>
        🛡️ SafeStep
      </h1>

      <div style={{ marginBottom: '1rem' }}>
        <p style={{ fontSize: '12px', color: '#888', marginBottom: '4px' }}>START POINT</p>
        <p style={{ fontSize: '14px', color: startPoint ? '#27ae60' : '#aaa' }}>
          {startPoint ? `${startPoint.lat.toFixed(4)}, ${startPoint.lng.toFixed(4)}` : 'Click on map to set'}
        </p>
      </div>

      <div style={{ marginBottom: '1.5rem' }}>
        <p style={{ fontSize: '12px', color: '#888', marginBottom: '4px' }}>END POINT</p>
        <p style={{ fontSize: '14px', color: endPoint ? '#e74c3c' : '#aaa' }}>
          {endPoint ? `${endPoint.lat.toFixed(4)}, ${endPoint.lng.toFixed(4)}` : 'Click on map to set'}
        </p>
      </div>

      {route && (
        <div style={{
          background: getDangerLevel(route.dangerScore).color + '20',
          border: `1px solid ${getDangerLevel(route.dangerScore).color}`,
          borderRadius: '10px',
          padding: '1rem',
          marginBottom: '1rem',
        }}>
          <p style={{ fontSize: '12px', color: '#888', marginBottom: '4px' }}>DANGER SCORE</p>
          <p style={{ fontSize: '24px', fontWeight: 'bold', color: getDangerLevel(route.dangerScore).color }}>
            {route.dangerScore}/10
          </p>
          <p style={{ fontSize: '13px', color: getDangerLevel(route.dangerScore).color }}>
            {getDangerLevel(route.dangerScore).label}
          </p>
        </div>
      )}
      <button
  onClick={onToggleReportMode}
  style={{
    width: '100%',
    padding: '12px',
    background: isReportMode ? '#e74c3c' : 'white',
    color: isReportMode ? 'white' : '#e74c3c',
    border: '1px solid #e74c3c',
    borderRadius: '10px',
    fontSize: '14px',
    cursor: 'pointer',
    marginBottom: '8px',
  }}
>
  {isReportMode ? '🚨 Exit Report Mode' : '🚨 Report Unsafe Area'}
</button>

      <button
        onClick={onFindRoute}
        disabled={!startPoint || !endPoint || isLoading}
        style={{
          width: '100%',
          padding: '12px',
          background: !startPoint || !endPoint ? '#ccc' : '#1a1a1a',
          color: 'white',
          border: 'none',
          borderRadius: '10px',
          fontSize: '14px',
          cursor: !startPoint || !endPoint ? 'not-allowed' : 'pointer',
          marginBottom: '8px',
        }}
      >
        {isLoading ? 'Finding Route...' : '🗺️ Find Safe Route'}
      </button>

      <button
        onClick={onClear}
        style={{
          width: '100%',
          padding: '12px',
          background: 'none',
          color: '#e74c3c',
          border: '1px solid #e74c3c',
          borderRadius: '10px',
          fontSize: '14px',
          cursor: 'pointer',
        }}
      >
        Clear
      </button>
    </div>
  );
}