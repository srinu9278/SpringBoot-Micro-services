package com.hotel.monitoring_service.controller;

import com.hotel.monitoring_service.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/services")
    public Map<String, Object> getServices() {
        return monitoringService.getCurrentServices();
    }

    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        return monitoringService.getServiceHealth();
    }

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        return monitoringService.getMetrics();
    }
}
