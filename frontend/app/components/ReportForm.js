'use client';

import { useState } from 'react';

export default function ReportForm({ position, onClose, onSubmit }) {
  const [reportType, setReportType] = useState('broken_light');
  const [description, setDescription] = useState('');
  const [severity, setSeverity] = useState(3);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  const reportTypes = [
    { value: 'broken_light', label: '💡 Broken Street Light' },
    { value: 'unsafe_area', label: '⚠️ Unsafe Area' },
    { value: 'harassment', label: '🚨 Harassment Reported' },
    { value: 'no_cctv', label: '📷 No CCTV Coverage' },
    { value: 'other', label: '📝 Other' },
  ];

  const handleSubmit = async () => {
    if (!description) {
      setMessage('Please add a description!');
      return;
    }
    setIsSubmitting(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      setMessage('Report submitted successfully!');
      setTimeout(() => {
        onSubmit({
          lat: position.lat,
          lng: position.lng,
          reportType,
          description,
          severity,
        });
        onClose();
      }, 1500);
    } catch (err) {
      setMessage('Failed to submit. Try again!');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{
      position: 'absolute',
      bottom: '20px',
      left: '20px',
      zIndex: 1000,
      background: 'white',
      borderRadius: '16px',
      padding: '1.5rem',
      width: '300px',
      boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 'bold', color: '#1a1a1a' }}>🚨 Report Unsafe Area</h3>
        <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px' }}>✕</button>
      </div>
      <p style={{ fontSize: '12px', color: '#888', marginBottom: '1rem' }}>
        📍 {position.lat.toFixed(4)}, {position.lng.toFixed(4)}
      </p>
      <div style={{ marginBottom: '1rem' }}>
        <p style={{ fontSize: '12px', color: '#888', marginBottom: '6px' }}>REPORT TYPE</p>
        <select
          value={reportType}
          onChange={(e) => setReportType(e.target.value)}
          style={{ width: '100%', padding: '8px', borderRadius: '8px', border: '1px solid #e0ddd5', fontSize: '13px' }}
        >
          {reportTypes.map(type => (
            <option key={type.value} value={type.value}>{type.label}</option>
          ))}
        </select>
      </div>
      <div style={{ marginBottom: '1rem' }}>
        <p style={{ fontSize: '12px', color: '#888', marginBottom: '6px' }}>SEVERITY (1-5)</p>
        <input
          type="range"
          min="1"
          max="5"
          value={severity}
          onChange={(e) => setSeverity(Number(e.target.value))}
          style={{ width: '100%' }}
        />
        <p style={{ fontSize: '12px', color: '#6c5ce7', textAlign: 'center' }}>
          {severity === 1 ? 'Very Low' : severity === 2 ? 'Low' : severity === 3 ? 'Medium' : severity === 4 ? 'High' : 'Very High'}
        </p>
      </div>
      <div style={{ marginBottom: '1rem' }}>
        <p style={{ fontSize: '12px', color: '#888', marginBottom: '6px' }}>DESCRIPTION</p>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Describe the unsafe situation..."
          rows={3}
          style={{ width: '100%', padding: '8px', borderRadius: '8px', border: '1px solid #e0ddd5', fontSize: '13px', resize: 'none', boxSizing: 'border-box' }}
        />
      </div>
      {message && (
        <p style={{ fontSize: '13px', color: message.includes('successfully') ? '#27ae60' : '#e74c3c', marginBottom: '1rem', textAlign: 'center' }}>
          {message}
        </p>
      )}
      <button
        onClick={handleSubmit}
        disabled={isSubmitting}
        style={{
          width: '100%',
          padding: '12px',
          background: '#e74c3c',
          color: 'white',
          border: 'none',
          borderRadius: '10px',
          fontSize: '14px',
          cursor: 'pointer',
        }}
      >
        {isSubmitting ? 'Submitting...' : '🚨 Submit Report'}
      </button>
    </div>
  );
}