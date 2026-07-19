import { API_CONFIG } from '../constants/config';

export const getSafeRoute = async (start, end) => {
  const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.safeRoute}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ start, end }),
  });

  if (!response.ok) {
    throw new Error('Failed to fetch safe route');
  }

  return response.json();
};