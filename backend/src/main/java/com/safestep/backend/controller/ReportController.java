package com.safestep.backend.controller;

import com.safestep.backend.model.Report;
import com.safestep.backend.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Report> submitReport(@Valid @RequestBody Report report) {
        Report saved = reportService.createReport(report);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Report>> getNearbyReports(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "1000") double radius) {
        return ResponseEntity.ok(reportService.getNearbyReports(latitude, longitude, radius));
    }
}
