# Qurilo Assessment - Microservices Order Management System

A comprehensive microservices-based order management system built with Spring Boot, featuring user authentication, product management, and order processing with Redis caching and PostgreSQL database.

## 🏗️ Architecture Overview

This project consists of three microservices:

- **Login Service** (Port: 8082) - User management and authentication
- **Product Service** (Port: 8080) - Product catalog and inventory management  
- **Order Service** (Port: 8081) - Order processing and management

### Technology Stack

- **Backend**: Spring Boot 3.5.4
- **Database**: PostgreSQL 15
- **Cache**: Redis
- **Email**: Spring Mail (SMTP)
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

- **Java 17 or higher**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **PostgreSQL 15** (if running locally)
- **Redis** (if running locally)

## 🚀 Quick Start with Docker Compose

The easiest way to run the entire system is using Docker Compose:

### 1. Clone the Repository
```bash
git clone <repository-url>
cd "Qurilo Assessment"
```

### 2. Configure Database Credentials (Optional)

**Default Credentials:**
- **PostgreSQL User**: `postgres`
- **PostgreSQL Password**: `root`

**To Change Credentials:**

#### Option A: Update Docker Compose File
Edit `Docker-compose.yml`:
```yaml
postgres:
  environment:
    POSTGRES_USER: your_custom_user
    POSTGRES_PASSWORD: your_custom_password
```

And update all services:
```yaml
order-service:
  environment:
    - DB_USERNAME=your_custom_user
    - DB_PASSWORD=your_custom_password

product-service:
  environment:
    - DB_USERNAME=your_custom_user
    - DB_PASSWORD=your_custom_password

login-service:
  environment:
    - DB_USERNAME=your_custom_user
    - DB_PASSWORD=your_custom_password
```



### 3. Build and Start All Services
```bash
# Build images with new configuration
docker-compose build

# Start all services
docker-compose up -d
```

This command will:
- Build all service images with updated configuration
- Start PostgreSQL database on port 5432
- Start Redis on port 6379
- Build and start all three microservices
- Set up the database schema automatically

### 4. Verify Services
Check if all services are running:
```bash
docker-compose ps
```

### 5. Access Services
- **Login Service**: http://localhost:8082
- **Product Service**: http://localhost:8080  
- **Order Service**: http://localhost:8081
- **Swagger UI**: 
  - Login: http://localhost:8082/swagger-ui.html
  - Product: http://localhost:8080/swagger-ui.html
  - Order: http://localhost:8081/swagger-ui.html

## 🛠️ Manual Setup (Without Docker)

If you prefer to run services individually:

### 1. Database Setup

#### PostgreSQL Setup
```bash
# Install PostgreSQL 15
# Create databases
createdb users
createdb products  
createdb orders

# Or use the provided init script
psql -U postgres -f init-databases.sql
```

#### Redis Setup
```bash
# Install Redis
redis-server
```

### 2. Environment Variables

Create `.env` files or set environment variables:

```bash
# Database Configuration (Default Values)
DB_USERNAME=postgres
DB_PASSWORD=root

# Redis Configuration  
REDIS_HOST=localhost
REDIS_PORT=6379

# Email Configuration (for Order Service)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# PostgreSQL Configuration (for Docker Compose)
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root
```

### 3. Build and Run Services

#### Login Service
```bash
cd login
mvn clean install
mvn spring-boot:run
```

#### Product Service
```bash
cd product-service
mvn clean install
mvn spring-boot:run
```

#### Order Service
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

## 🔧 Custom Database Configuration

### Changing Database Credentials

If you want to use custom database credentials, follow these steps:

#### Step 1: Update Docker Compose Configuration
Edit `Docker-compose.yml` and change:
```yaml
postgres:
  environment:
    POSTGRES_USER: your_new_username
    POSTGRES_PASSWORD: your_new_password
```

#### Step 2: Update Service Environment Variables
In the same file, update all services:
```yaml
order-service:
  environment:
    - DB_USERNAME=your_new_username
    - DB_PASSWORD=your_new_password

product-service:
  environment:
    - DB_USERNAME=your_new_username
    - DB_PASSWORD=your_new_password

login-service:
  environment:
    - DB_USERNAME=your_new_username
    - DB_PASSWORD=your_new_password
```

#### Step 3: Rebuild and Start
```bash
# Stop existing containers
docker-compose down

# Remove old volumes (important for new credentials)
docker-compose down -v

# Build with new configuration
docker-compose build

# Start services
docker-compose up -d
```



## 📧 Email Notification System

### Features
- **Asynchronous Email Sending**: Uses `CompletableFuture.runAsync()` for non-blocking email delivery
- **HTML Email Templates**: Beautiful, responsive HTML email templates with CSS styling
- **Template Engine**: Separate HTML template files with placeholder replacement
- **Fallback Mechanism**: Plain text email if HTML template fails to load
- **INR Currency Support**: All amounts displayed in Indian Rupees (₹)

### Email Configuration
The Order Service includes email functionality for order confirmations:

#### Gmail SMTP Configuration
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:your-email@gmail.com}
    password: ${MAIL_PASSWORD:your-app-password}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          timeout: 10000
          connectiontimeout: 10000
          writetimeout: 10000
        transport:
          protocol: smtp
```

#### Email Template Location
- **Template File**: `order-service/src/main/resources/templates/order-confirmation-email.html`
- **Template Variables**:
  - `{{userName}}` - User's full name
  - `{{orderId}}` - Order ID with # prefix
  - `{{productName}}` - Product name
  - `{{quantity}}` - Order quantity
  - `{{unitPrice}}` - Unit price in ₹
  - `{{status}}` - Order status with badge
  - `{{orderDate}}` - Order creation date
  - `{{totalAmount}}` - Total amount in ₹



### Setting Up Email (Gmail)
1. **Enable 2-Step Verification** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account → Security → App Passwords
   - Select "Mail" as the app
   - Copy the 16-character password
3. **Set Environment Variables**:
   ```bash
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-16-character-app-password
   ```

## 📊 Database Schema

### Users Database
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Products Database
```sql
CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_description TEXT,
    product_price DECIMAL(10,2) NOT NULL,
    product_stock_quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Orders Database
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 API Endpoints

### Login Service (Port: 8082)

#### User Management
- `POST /api/v1/users/register` - Register new user
- `GET /api/v1/users/{userId}` - Get user by ID
- `GET /api/v1/users/details/{usernameOrEmail}` - Get user details

### Product Service (Port: 8080)

#### Product Management
- `POST /api/v1/products` - Create new product
- `GET /api/v1/products` - Get all products
- `GET /api/v1/products/{productId}` - Get product by ID
- `PUT /api/v1/products/{productId}` - Update product
- `DELETE /api/v1/products/{productId}` - Delete product
- `PUT /api/v1/products/{productId}/stock` - Update product stock
- `GET /api/v1/products/{productId}/check-stock` - Check product stock

### Order Service (Port: 8081)

#### Order Management
- `POST /api/v1/orders` - Create new order
- `GET /api/v1/orders` - Get all orders
- `GET /api/v1/orders?search={searchTerm}` - Search orders

## 🔄 Service Communication

### Order Processing Flow
1. **User Verification**: Order service calls login service to verify user exists
2. **Product Validation**: Order service calls product service to get product details
3. **Stock Check**: Order service validates product stock availability
4. **Order Creation**: Order is saved to database
5. **Stock Update**: Product stock is updated via product service
6. **Cache Invalidation**: Product cache is cleared
7. **Email Notification**: Order confirmation email is sent asynchronously

### Inter-Service Communication
- **Order Service ↔ Login Service**: User verification
- **Order Service ↔ Product Service**: Product details and stock management
- **All Services ↔ Redis**: Caching layer
- **All Services ↔ PostgreSQL**: Data persistence

## 🗄️ Caching Strategy

### Redis Configuration
- **Cache Type**: Redis
- **Cache Keys**:
  - `users::{userId}` - User data caching
  - `products::{productId}` - Product data caching
- **TTL**: Configurable per service
- **Connection**: Health checks every 5 seconds

## 📝 Sample API Requests

### Register User
```bash
curl -X POST http://localhost:8082/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD001",
    "productName": "Laptop",
    "productDescription": "High-performance laptop",
    "productPrice": 999.99,
    "productStockQuantity": 50
  }'
```

### Create Order
```bash
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": "PROD001",
    "quantity": 2
  }'
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose logs postgres

# Verify database exists
psql -U postgres -l
```

#### 2. Redis Connection Issues
```bash
# Check Redis status
docker-compose logs mini_order_redis

# Test Redis connection
redis-cli ping
```

#### 3. Service Communication Issues
```bash
# Check service logs
docker-compose logs order-service
docker-compose logs product-service
docker-compose logs login-service
```

#### 4. Email Configuration Issues
```bash
# Check email configuration
# Verify Gmail App Password is correct
# Ensure 2-Step Verification is enabled
# Check SMTP settings in application.yml

# Test email functionality
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "productId": "PROD001", "quantity": 1}'
```

#### 5. Port Conflicts
If ports are already in use:
```bash
# Stop existing services
docker-compose down

# Or change ports in docker-compose.yml
```

### Health Checks
- **Database**: `pg_isready -U postgres`
- **Redis**: `redis-cli ping`
- **Services**: Check `/actuator/health` endpoints

## 📁 Project Structure

```
Qurilo Assessment/
├── Docker-compose.yml          # Docker orchestration
├── init-databases.sql          # Database initialization
├── login/                      # Login Service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/qurilo/login/
│       ├── config/             # Configuration classes
│       ├── controller/         # REST controllers
│       ├── dto/               # Data Transfer Objects
│       ├── entity/            # JPA entities
│       ├── exception/         # Custom exceptions
│       ├── repository/        # Data repositories
│       ├── responses/         # Response models
│       ├── services/          # Business logic
│       └── utils/             # Utility classes
├── product-service/            # Product Service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/qurilo/product_service/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── exceptions/
│       ├── repository/
│       ├── service/
│       └── utils/
├── order-service/              # Order Service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/qurilo/order_service/
│       ├── client/            # Service clients
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── exception/
│       ├── repository/
│       ├── service/
│       └── utils/
│   └── src/main/resources/
│       └── templates/         # Email templates
│           └── order-confirmation-email.html
└── logs/                       # Application logs
```

## 🔒 Security Features

- **Input Validation**: Bean validation annotations
- **Exception Handling**: Global exception handlers
- **Data Sanitization**: Proper data encoding
- **Service Authentication**: Inter-service verification

## 📈 Monitoring & Logging

- **Application Logs**: Stored in `logs/` directory
- **Redis Monitoring**: Connection status logging
- **Database Monitoring**: Connection pool metrics

---

**Happy Coding! 🎉** 