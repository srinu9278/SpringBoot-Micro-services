# MongoDB Setup for Notification Service

## Why MongoDB for Notifications?

### **Advantages over PostgreSQL:**

1. **Document Structure**: Perfect for varying notification payloads
2. **TTL Indexes**: Automatic cleanup of expired notifications
3. **Flexible Schema**: Easy to add new notification types
4. **High Performance**: Better for high-volume writes
5. **JSON Native**: Natural fit for notification data
6. **Horizontal Scaling**: Easy to scale for large volumes

## Installation & Setup

### **1. Install MongoDB**

#### Windows:
```bash
# Download from https://www.mongodb.com/try/download/community
# Or use Chocolatey
choco install mongodb

# Start MongoDB service
net start MongoDB
```

#### macOS:
```bash
# Using Homebrew
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb/brew/mongodb-community
```

#### Linux (Ubuntu):
```bash
# Import MongoDB public key
wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | sudo apt-key add -

# Add MongoDB repository
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Install MongoDB
sudo apt-get update
sudo apt-get install -y mongodb-org

# Start MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod
```

### **2. Create Database and Collections**

```javascript
// Connect to MongoDB
mongo

// Create database
use hotel_notifications

// Create collections with indexes
db.createCollection("notifications")

// Create indexes for better performance
db.notifications.createIndex({ "notificationReference": 1 }, { unique: true })
db.notifications.createIndex({ "recipient": 1 })
db.notifications.createIndex({ "channel": 1 })
db.notifications.createIndex({ "externalId": 1 })
db.notifications.createIndex({ "scheduledAt": 1 })
db.notifications.createIndex({ "expiresAt": 1 }, { expireAfterSeconds: 0 })
db.notifications.createIndex({ "createdBy": 1 })
db.notifications.createIndex({ "relatedEntityType": 1 })
db.notifications.createIndex({ "createdAt": 1 })
db.notifications.createIndex({ "status": 1 })
db.notifications.createIndex({ "type": 1 })
db.notifications.createIndex({ "priority": 1 })
db.notifications.createIndex({ "eventType": 1 })

// Text search index
db.notifications.createIndex({ 
    "subject": "text", 
    "message": "text" 
})

// Compound indexes for common queries
db.notifications.createIndex({ "status": 1, "createdAt": -1 })
db.notifications.createIndex({ "type": 1, "status": 1 })
db.notifications.createIndex({ "recipient": 1, "status": 1 })
db.notifications.createIndex({ "eventType": 1, "status": 1 })
```

### **3. Environment Configuration**

```bash
# Set MongoDB connection string
export MONGODB_URI=mongodb://localhost:27017/hotel_notifications

# Or in application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/hotel_notifications
```

## MongoDB-Specific Features

### **1. TTL (Time To Live) Indexes**

```javascript
// Automatic deletion of expired notifications
db.notifications.createIndex(
    { "expiresAt": 1 }, 
    { expireAfterSeconds: 0 }
)
```

### **2. Text Search**

```javascript
// Full-text search across message and subject
db.notifications.createIndex({ 
    "subject": "text", 
    "message": "text" 
})

// Search example
db.notifications.find({ 
    $text: { $search: "booking confirmation" } 
})
```

### **3. Aggregation Pipeline Examples**

```javascript
// Count notifications by status
db.notifications.aggregate([
    { $group: { _id: "$status", count: { $sum: 1 } } }
])

// Count notifications by type and status
db.notifications.aggregate([
    { $group: { 
        _id: { type: "$type", status: "$status" }, 
        count: { $sum: 1 } 
    }}
])

// Average retry count by status
db.notifications.aggregate([
    { $group: { 
        _id: "$status", 
        avgRetryCount: { $avg: "$retryCount" } 
    }}
])

// Notifications by date range
db.notifications.aggregate([
    { $match: { 
        createdAt: { 
            $gte: ISODate("2025-09-19T00:00:00Z"),
            $lte: ISODate("2025-09-19T23:59:59Z")
        }
    }},
    { $group: { _id: "$type", count: { $sum: 1 } } }
])
```

### **4. Complex Queries**

```javascript
// Find notifications with specific metadata
db.notifications.find({
    "metadata.bookingId": "123",
    "metadata.hotelId": "456"
})

// Find notifications by template data
db.notifications.find({
    "templateData.guestName": "John Doe"
})

// Find failed notifications for retry
db.notifications.find({
    status: "FAILED",
    retryCount: { $lt: "$maxRetries" },
    createdAt: { $gte: new Date(Date.now() - 3600000) }
})
```

## Performance Optimization

### **1. Index Strategy**

```javascript
// Compound indexes for common query patterns
db.notifications.createIndex({ "status": 1, "createdAt": -1 })
db.notifications.createIndex({ "type": 1, "status": 1, "createdAt": -1 })
db.notifications.createIndex({ "recipient": 1, "status": 1, "createdAt": -1 })
db.notifications.createIndex({ "eventType": 1, "status": 1, "createdAt": -1 })
```

### **2. Sharding (for large scale)**

```javascript
// Enable sharding
sh.enableSharding("hotel_notifications")

// Shard by recipient for even distribution
sh.shardCollection("hotel_notifications.notifications", { "recipient": 1 })
```

### **3. Replica Set (for high availability)**

```javascript
// Initialize replica set
rs.initiate({
    _id: "rs0",
    members: [
        { _id: 0, host: "localhost:27017" },
        { _id: 1, host: "localhost:27018" },
        { _id: 2, host: "localhost:27019" }
    ]
})
```

## Monitoring & Maintenance

### **1. Database Stats**

```javascript
// Collection statistics
db.notifications.stats()

// Index usage
db.notifications.aggregate([{ $indexStats: {} }])
```

### **2. Cleanup Operations**

```javascript
// Remove old notifications (older than 30 days)
db.notifications.deleteMany({
    createdAt: { $lt: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000) }
})

// Remove failed notifications older than 7 days
db.notifications.deleteMany({
    status: "FAILED",
    createdAt: { $lt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) }
})
```

### **3. Backup & Restore**

```bash
# Backup
mongodump --db hotel_notifications --out /backup/path

# Restore
mongorestore --db hotel_notifications /backup/path/hotel_notifications
```

## Sample Data

```javascript
// Insert sample notification
db.notifications.insertOne({
    notificationReference: "NOTIF_1758268062000_ABC12345",
    type: "SLACK",
    status: "PENDING",
    priority: "NORMAL",
    eventType: "BOOKING_CREATED",
    recipient: "#bookings",
    subject: "New Booking Created",
    message: "A new booking has been created for Room 101",
    channel: "#bookings",
    retryCount: 0,
    maxRetries: 3,
    createdBy: "system",
    relatedEntityType: "booking",
    relatedEntityId: "123",
    templateData: {
        guestName: "John Doe",
        roomNumber: "101",
        checkInDate: "2025-09-20",
        checkOutDate: "2025-09-22"
    },
    metadata: {
        source: "booking-service",
        version: "1.0",
        environment: "production"
    },
    createdAt: new Date(),
    updatedAt: new Date()
})
```

## Benefits of MongoDB for Notifications

1. **Flexible Schema**: Easy to add new notification types
2. **High Performance**: Optimized for write-heavy workloads
3. **TTL Indexes**: Automatic cleanup of expired notifications
4. **Text Search**: Full-text search across notification content
5. **Aggregation**: Complex analytics on notification patterns
6. **Horizontal Scaling**: Easy to scale for high volumes
7. **JSON Native**: Perfect for notification payloads
8. **No Schema Migrations**: Easy to evolve notification structure

This MongoDB setup provides a robust, scalable foundation for your notification service! 🚀


