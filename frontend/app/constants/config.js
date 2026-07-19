export const MAP_CONFIG = {
  defaultCenter: [28.6139, 77.2090], // Delhi coordinates
  defaultZoom: 13,
  minZoom: 10,
  maxZoom: 18,
};

export const API_CONFIG = {
  baseUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  endpoints: {
    safeRoute: '/api/route/safe',
    reports: '/api/reports',
    chat: '/api/chat/explain',
  },
};

export const DANGER_LEVELS = {
  LOW: { label: 'Low Risk', color: '#27ae60' },
  MEDIUM: { label: 'Medium Risk', color: '#f39c12' },
  HIGH: { label: 'High Risk', color: '#e74c3c' },
};