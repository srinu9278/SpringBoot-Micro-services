package com.hotel.monitoring_service.service;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Map;

public interface MonitoringService {
    
    // Real-time monitoring
    Map<String, Object> getCurrentServices();
    Map<String, Object> getServiceHealth();
    Map<String, Object> getMetrics();
    
    // Service discovery
    List<ServiceInstance> getServiceInstances(String serviceName);
    boolean isServiceHealthy(String serviceName);
}
