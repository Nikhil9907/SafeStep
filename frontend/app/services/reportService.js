import { API_CONFIG } from '../constants/config';

export const submitReport = async (reportData) => {
  const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.reports}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reportData),
  });

  if (!response.ok) {
    throw new Error('Failed to submit report');
  }

  return response.json();
};

export const getReports = async () => {
  const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.reports}`);

  if (!response.ok) {
    throw new Error('Failed to fetch reports');
  }

  return response.json();
};