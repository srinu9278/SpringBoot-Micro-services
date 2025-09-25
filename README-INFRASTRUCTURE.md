# 🏗️ Hotel Management System - Infrastructure Setup

## Overview
This document explains how to set up and run the infrastructure services for the Hotel Management System microservices architecture.

## Services Created

### 1. Service Discovery (Eureka Server)
- **Port**: 8761
- **Purpose**: Service registration and discovery
- **URL**: http://localhost:8761
- **Location**: `service-discovery/`

### 2. API Gateway
- **Port**: 8080
- **Purpose**: Single entry point for all microservices
- **URL**: http://localhost:8080
- **Location**: `api-gateway/`

### 3. User Service (Updated)
- **Port**: 9000
- **Purpose**: User management (existing service, now registered with Eureka)
- **URL**: http://localhost:9000
- **Location**: `user-service/`

## Quick Start

### Option 1: Using Batch Scripts (Windows)
```bash
# Start all services
start-infrastructure.bat

# Test the infrastructure
test-infrastructure.bat
```

### Option 2: Manual Start
```bash
# Terminal 1 - Start Service Discovery
cd service-discovery
mvn spring-boot:run

# Terminal 2 - Start API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 3 - Start User Service
cd user-service
mvn spring-boot:run
```

## Service URLs

### Service Discovery (Eureka Dashboard)
- **URL**: http://localhost:8761
- **Purpose**: View registered services
- **Features**: Service health, instances, metadata

### API Gateway
- **Base URL**: http://localhost:8080
- **User Service**: http://localhost:8080/api/v2/user/**
- **Health Check**: http://localhost:8080/actuator/health

### User Service (Direct Access)
- **Base URL**: http://localhost:9000
- **APIs**: http://localhost:9000/api/v2/user/**
- **Health Check**: http://localhost:9000/actuator/health

## API Endpoints

### Through API Gateway (Recommended)
```
GET    http://localhost:8080/api/v2/user/getAll
POST   http://localhost:8080/api/v2/user/register
GET    http://localhost:8080/api/v2/user/{id}
PATCH  http://localhost:8080/api/v2/user/update
DELETE http://localhost:8080/api/v2/user/{id}
```

### Direct Access to User Service
```
GET    http://localhost:9000/api/v2/user/getAll
POST   http://localhost:9000/api/v2/user/register
GET    http://localhost:9000/api/v2/user/{id}
PATCH  http://localhost:9000/api/v2/user/update
DELETE http://localhost:9000/api/v2/user/{id}
```

## Configuration

### Service Discovery Configuration
- **File**: `service-discovery/src/main/resources/application.yml`
- **Port**: 8761
- **Self-registration**: Disabled (server doesn't register itself)

### API Gateway Configuration
- **File**: `api-gateway/src/main/resources/application.yml`
- **Port**: 8080
- **Routes**: Configured for all planned services
- **CORS**: Enabled for frontend (localhost:5173)

### User Service Configuration
- **File**: `user-service/src/main/resources/application.yaml`
- **Port**: 9000
- **Eureka Client**: Enabled
- **Database**: PostgreSQL (users database)

## Troubleshooting

### Common Issues

1. **Service Discovery Not Starting**
   - Check if port 8761 is available
   - Verify Java 21 is installed
   - Check Maven dependencies

2. **API Gateway Not Routing**
   - Ensure Service Discovery is running
   - Check if User Service is registered
   - Verify route configurations

3. **User Service Not Registering**
   - Check Eureka client configuration
   - Verify Service Discovery is running
   - Check network connectivity

### Health Checks

```bash
# Check Service Discovery
curl http://localhost:8761/actuator/health

# Check API Gateway
curl http://localhost:8080/actuator/health

# Check User Service
curl http://localhost:9000/actuator/health
```

### Logs
- Check console output for each service
- Look for registration messages in User Service
- Check routing logs in API Gateway

## Next Steps

1. **Verify Infrastructure**: Run test scripts to ensure everything works
2. **Create Hotel Service**: Next microservice to implement
3. **Add More Services**: Room, Booking, Payment, Notification
4. **Frontend Integration**: Update React app to use API Gateway

## Development Workflow

1. **Start Infrastructure**: Always start Service Discovery first
2. **Start Services**: Start individual microservices
3. **Test Through Gateway**: Use API Gateway URLs for testing
4. **Monitor**: Use Eureka dashboard to monitor services

## Architecture Benefits

- **Single Entry Point**: All requests go through API Gateway
- **Service Discovery**: Automatic service registration and discovery
- **Load Balancing**: Built-in load balancing for multiple instances
- **Health Monitoring**: Centralized health checks
- **CORS Handling**: Centralized CORS configuration
- **Future Ready**: Easy to add new services

---

**Note**: Make sure PostgreSQL is running and the `users` database exists before starting the User Service.
