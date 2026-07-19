import { useState, useCallback } from 'react';
import { MAP_CONFIG } from '../constants/config';

export const useMap = () => {
  const [map, setMap] = useState(null);
  const [startPoint, setStartPoint] = useState(null);
  const [endPoint, setEndPoint] = useState(null);
  const [routeData, setRouteData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [reports, setReports] = useState([]);

  const initializeMap = useCallback((mapInstance) => {
    setMap(mapInstance);
  }, []);

  const setStart = useCallback((latlng) => {
    setStartPoint(latlng);
  }, []);

  const setEnd = useCallback((latlng) => {
    setEndPoint(latlng);
  }, []);

  const setRoute = useCallback((routeData) => {
    setRouteData(routeData);
  }, []);
  const addReport = useCallback((report) => {
    setReports(prev => [...prev, report]);
  }, []);

  const clearRoute = useCallback(() => {
    setRoute(null);
    setStartPoint(null);
    setEndPoint(null);
    setError(null);
  }, []);

  return {
    map,
    startPoint,
    endPoint,
    route: routeData,
    isLoading,
    error,
    initializeMap,
    setStart,
    setRoute,
    setEnd,
    addReport,
    clearRoute,
  };
};