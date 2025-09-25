package com.hotel.monitoring_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MonitoringServiceImpl implements MonitoringService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public Map<String, Object> getCurrentServices() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalServices", discoveryClient.getServices().size());
        response.put("services", discoveryClient.getServices());
        
        Map<String, List<ServiceInstance>> serviceInstances = new HashMap<>();
        for (String service : discoveryClient.getServices()) {
            serviceInstances.put(service, discoveryClient.getInstances(service));
        }
        response.put("serviceInstances", serviceInstances);
        
        return response;
    }

    @Override
    public Map<String, Object> getServiceHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", "UP");
        response.put("services", discoveryClient.getServices());
        return response;
    }

    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalServices", discoveryClient.getServices().size());
        response.put("uptime", "Running");
        response.put("memoryUsage", "N/A");
        response.put("cpuUsage", "N/A");
        return response;
    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }

    @Override
    public boolean isServiceHealthy(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return !instances.isEmpty();
    }
}
