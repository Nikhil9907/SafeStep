package com.safestep.backend.controller;

import com.safestep.backend.model.CctvData;
import com.safestep.backend.model.CrimeData;
import com.safestep.backend.model.LightingData;
import com.safestep.backend.repository.CctvDataRepository;
import com.safestep.backend.repository.CrimeDataRepository;
import com.safestep.backend.repository.LightingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class MapDataController {

    private final LightingDataRepository lightingRepository;
    private final CctvDataRepository cctvRepository;
    private final CrimeDataRepository crimeRepository;

    @Autowired
    public MapDataController(
            LightingDataRepository lightingRepository,
            CctvDataRepository cctvRepository,
            CrimeDataRepository crimeRepository) {
        this.lightingRepository = lightingRepository;
        this.cctvRepository = cctvRepository;
        this.crimeRepository = crimeRepository;
    }

    @GetMapping("/lighting")
    public ResponseEntity<List<LightingData>> getLightingData(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "2000") double radius) {
        
        List<LightingData> all = lightingRepository.findAll();
        if (latitude != null && longitude != null) {
            List<LightingData> nearby = new ArrayList<>();
            for (LightingData item : all) {
                if ("WORKING".equalsIgnoreCase(item.getStatus()) && 
                    haversineDistance(latitude, longitude, item.getLatitude(), item.getLongitude()) <= radius) {
                    nearby.add(item);
                }
            }
            return ResponseEntity.ok(nearby);
        }
        return ResponseEntity.ok(all);
    }

    @GetMapping("/cctv")
    public ResponseEntity<List<CctvData>> getCctvData(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "2000") double radius) {
        
        List<CctvData> all = cctvRepository.findAll();
        if (latitude != null && longitude != null) {
            List<CctvData> nearby = new ArrayList<>();
            for (CctvData item : all) {
                if ("ACTIVE".equalsIgnoreCase(item.getStatus()) && 
                    haversineDistance(latitude, longitude, item.getLatitude(), item.getLongitude()) <= radius) {
                    nearby.add(item);
                }
            }
            return ResponseEntity.ok(nearby);
        }
        return ResponseEntity.ok(all);
    }

    @GetMapping("/crime")
    public ResponseEntity<List<CrimeData>> getCrimeData(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "2000") double radius) {
        
        List<CrimeData> all = crimeRepository.findAll();
        if (latitude != null && longitude != null) {
            List<CrimeData> nearby = new ArrayList<>();
            for (CrimeData item : all) {
                if (haversineDistance(latitude, longitude, item.getLatitude(), item.getLongitude()) <= radius) {
                    nearby.add(item);
                }
            }
            return ResponseEntity.ok(nearby);
        }
        return ResponseEntity.ok(all);
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
}
