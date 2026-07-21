package com.safestep.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safestep.backend.model.CctvData;
import com.safestep.backend.model.CrimeData;
import com.safestep.backend.model.LightingData;
import com.safestep.backend.model.Report;
import com.safestep.backend.repository.CctvDataRepository;
import com.safestep.backend.repository.CrimeDataRepository;
import com.safestep.backend.repository.LightingDataRepository;
import com.safestep.backend.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RouteScoringService {

    private final ReportRepository reportRepository;
    private final CrimeDataRepository crimeDataRepository;
    private final CctvDataRepository cctvDataRepository;
    private final LightingDataRepository lightingDataRepository;
    private final OpenAIService openAIService;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public RouteScoringService(
            ReportRepository reportRepository,
            CrimeDataRepository crimeDataRepository,
            CctvDataRepository cctvDataRepository,
            LightingDataRepository lightingDataRepository,
            OpenAIService openAIService) {
        this.reportRepository = reportRepository;
        this.crimeDataRepository = crimeDataRepository;
        this.cctvDataRepository = cctvDataRepository;
        this.lightingDataRepository = lightingDataRepository;
        this.openAIService = openAIService;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoordinateDto {
        private double latitude;
        private double longitude;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteInfo {
        private List<CoordinateDto> coordinates;
        private double distance; // meters
        private double duration; // seconds
        private double dangerScore; // 0 - 100
        private String dangerLevel; // LOW, MEDIUM, HIGH
        private long crimeCount;
        private long cctvCount;
        private long lightingCount;
        private long reportCount;
        private String explanation;
    }

    public List<RouteInfo> computeAndRankRoutes(double startLat, double startLon, double endLat, double endLon) {
        List<RouteInfo> routes = new ArrayList<>();
        String url = String.format(Locale.US, 
                "http://router.project-osrm.org/route/v1/foot/%f,%f;%f,%f?alternatives=true&geometries=geojson&overview=full",
                startLon, startLat, endLon, endLat);

        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                JsonNode root = objectMapper.readTree(body);
                JsonNode routesNode = root.path("routes");
                if (routesNode.isArray() && routesNode.size() > 0) {
                    for (JsonNode routeNode : routesNode) {
                        RouteInfo routeInfo = parseAndScoreRoute(routeNode);
                        if (routeInfo != null) {
                            routes.add(routeInfo);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("OSRM routing API unreachable or failed. Falling back to mock routes. Error: " + e.getMessage());
        }

        // Fallback to mock routes if no routes are returned
        if (routes.isEmpty()) {
            routes = generateMockRoutes(startLat, startLon, endLat, endLon);
        }

        // Rank routes: safety first (dangerScore ascending), then distance
        routes.sort((r1, r2) -> {
            int cmp = Double.compare(r1.getDangerScore(), r2.getDangerScore());
            if (cmp != 0) return cmp;
            return Double.compare(r1.getDistance(), r2.getDistance());
        });

        return routes;
    }

    private RouteInfo parseAndScoreRoute(JsonNode routeNode) {
        try {
            double distance = routeNode.path("distance").asDouble();
            double duration = routeNode.path("duration").asDouble();
            
            JsonNode geomNode = routeNode.path("geometry");
            JsonNode coordsNode = geomNode.path("coordinates");
            
            if (!coordsNode.isArray() || coordsNode.isEmpty()) {
                return null;
            }

            List<CoordinateDto> coordinates = new ArrayList<>();
            for (JsonNode coordNode : coordsNode) {
                double lon = coordNode.get(0).asDouble();
                double lat = coordNode.get(1).asDouble();
                coordinates.add(new CoordinateDto(lat, lon));
            }

            return scoreRouteCoordinates(coordinates, distance, duration);

        } catch (Exception e) {
            System.err.println("Error parsing and scoring route: " + e.getMessage());
            return null;
        }
    }

    private RouteInfo scoreRouteCoordinates(List<CoordinateDto> coordinates, double distance, double duration) {
        // Fetch all active/relevant safety points from databases
        List<CrimeData> crimes = crimeDataRepository.findAll();
        List<Report> reports = reportRepository.findAll();
        List<CctvData> cctvs = cctvDataRepository.findByStatus("ACTIVE");
        List<LightingData> lightings = lightingDataRepository.findByStatus("WORKING");

        // Filter and count how many points are close to the route coordinates path
        long crimeCount = crimes.stream().filter(c -> isNearRoute(c.getLatitude(), c.getLongitude(), coordinates, 50.0)).count();
        long reportCount = reports.stream().filter(r -> isNearRoute(r.getLatitude(), r.getLongitude(), coordinates, 40.0)).count();
        long cctvCount = cctvs.stream().filter(cam -> isNearRoute(cam.getLatitude(), cam.getLongitude(), coordinates, 30.0)).count();
        long lightingCount = lightings.stream().filter(l -> isNearRoute(l.getLatitude(), l.getLongitude(), coordinates, 25.0)).count();

        // Safety score computation: base of 15, penalize hazards, reward safe markers
        double rawScore = 15.0 + (crimeCount * 25.0) + (reportCount * 15.0) - (cctvCount * 12.0) - (lightingCount * 8.0);
        double dangerScore = Math.max(0.0, Math.min(100.0, rawScore));

        String dangerLevel;
        if (dangerScore <= 35.0) {
            dangerLevel = "LOW";
        } else if (dangerScore <= 75.0) {
            dangerLevel = "MEDIUM";
        } else {
            dangerLevel = "HIGH";
        }

        // Call AI Explainer
        String explanation = openAIService.explainRoute(dangerScore, crimeCount, cctvCount, lightingCount, reportCount);

        return new RouteInfo(
                coordinates,
                distance,
                duration,
                dangerScore,
                dangerLevel,
                crimeCount,
                cctvCount,
                lightingCount,
                reportCount,
                explanation
        );
    }

    private boolean isNearRoute(double itemLat, double itemLon, List<CoordinateDto> routeCoords, double radiusMeters) {
        for (CoordinateDto coord : routeCoords) {
            if (haversineDistance(itemLat, itemLon, coord.getLatitude(), coord.getLongitude()) <= radiusMeters) {
                return true;
            }
        }
        return false;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private List<RouteInfo> generateMockRoutes(double startLat, double startLon, double endLat, double endLon) {
        List<RouteInfo> mockRoutes = new ArrayList<>();
        
        // Distance and duration calculation
        double latDiff = endLat - startLat;
        double lonDiff = endLon - startLon;
        double distance = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111000.0;
        double duration = distance / 1.4; // walking speed

        // Route 1
        List<CoordinateDto> path1 = new ArrayList<>();
        path1.add(new CoordinateDto(startLat, startLon));
        path1.add(new CoordinateDto(startLat + latDiff * 0.5, startLon + lonDiff * 0.5));
        path1.add(new CoordinateDto(endLat, endLon));
        mockRoutes.add(scoreRouteCoordinates(path1, distance, duration));

        // Route 2
        List<CoordinateDto> path2 = new ArrayList<>();
        path2.add(new CoordinateDto(startLat, startLon));
        path2.add(new CoordinateDto(startLat + latDiff * 0.5 + 0.002, startLon + lonDiff * 0.5 - 0.002));
        path2.add(new CoordinateDto(endLat, endLon));
        mockRoutes.add(scoreRouteCoordinates(path2, distance * 1.25, duration * 1.25));

        return mockRoutes;
    }
}
