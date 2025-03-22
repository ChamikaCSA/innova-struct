package com.example.backend.controller;

import com.example.backend.service.BidAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/analytics/bids")
public class BidAnalyticsController {

    @Autowired
    private BidAnalyticsService bidAnalyticsService;

    @GetMapping("/success-rate/{companyId}")
    public ResponseEntity<Map<String, Object>> getBidSuccessRate(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(bidAnalyticsService.getBidSuccessRate(companyId, months));
    }

    @GetMapping("/volume/{companyId}")
    public ResponseEntity<Map<String, Object>> getBidVolume(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(bidAnalyticsService.getBidVolume(companyId, months));
    }

    @GetMapping("/distribution/{companyId}")
    public ResponseEntity<Map<String, Object>> getBidDistribution(
            @PathVariable String companyId) {
        return ResponseEntity.ok(bidAnalyticsService.getBidDistribution(companyId));
    }

    @GetMapping("/statistics/{companyId}")
    public ResponseEntity<Map<String, Object>> getBidStatistics(
            @PathVariable String companyId) {
        return ResponseEntity.ok(bidAnalyticsService.getBidStatistics(companyId));
    }

    @GetMapping("/performance/{companyId}")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(
            @PathVariable String companyId) {
        return ResponseEntity.ok(bidAnalyticsService.getPerformanceMetrics(companyId));
    }

    @GetMapping("/trends/{companyId}")
    public ResponseEntity<Map<String, Object>> getBidTrends(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "month") String timeframe) {
        return ResponseEntity.ok(bidAnalyticsService.getBidTrends(companyId, timeframe));
    }

    @GetMapping("/projects/timelines")
    public ResponseEntity<Map<String, Object>> getProjectTimelines(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(bidAnalyticsService.getProjectTimelines(type));
    }
}