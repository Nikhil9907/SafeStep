package com.safestep.backend.controller;

import com.safestep.backend.service.RouteScoringService;
import com.safestep.backend.service.RouteScoringService.RouteInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final RouteScoringService routeScoringService;

    @Autowired
    public RouteController(RouteScoringService routeScoringService) {
        this.routeScoringService = routeScoringService;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteRequest {
        @NotNull(message = "Start latitude is required")
        private Double startLatitude;

        @NotNull(message = "Start longitude is required")
        private Double startLongitude;

        @NotNull(message = "End latitude is required")
        private Double endLatitude;

        @NotNull(message = "End longitude is required")
        private Double endLongitude;
    }

    @PostMapping("/safe")
    public ResponseEntity<List<RouteInfo>> getSafeRoutes(@Valid @RequestBody RouteRequest request) {
        List<RouteInfo> rankedRoutes = routeScoringService.computeAndRankRoutes(
                request.getStartLatitude(),
                request.getStartLongitude(),
                request.getEndLatitude(),
                request.getEndLongitude()
        );
        return ResponseEntity.ok(rankedRoutes);
    }
}
