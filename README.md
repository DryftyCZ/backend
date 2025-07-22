# Ticketing System Backend

Spring Boot based REST API for a comprehensive event ticketing platform with QR code generation, OAuth2 authentication, and real-time analytics.

## Technologies

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **H2 Database** (development) / PostgreSQL (production)
- **OAuth2** (Google, Facebook integration)
- **QR Code Generation** (Google ZXing)
- **Maven** build system

## Features

### Core Functionality
- 🔐 **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - OAuth2 social login (Google, Facebook)
  - Role-based access control (ADMIN, ORGANIZER, WORKER, VISITOR)
  - Invite token system for organizers

- 🎫 **Event Management**
  - Full CRUD operations for events
  - Multiple ticket types per event
  - Organizer assignment and commission tracking

- 🎟️ **Ticket System**
  - QR code generation for tickets
  - Ticket purchase and validation
  - Real-time availability tracking
  - Different pricing tiers

- 📊 **Analytics**
  - Real-time event statistics
  - Visitor tracking and geolocation
  - Sales analytics
  - Dashboard metrics

## Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Port 8080 available

## Quick Start

1. **Clone the repository**
   ```bash
   git clone [your-repo-url]
   cd backend
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

3. **Access the application**
   - API: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:file:./data/ticketing-db`
     - Username: `sa`
     - Password: `password`

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Logout user

### Events
- `GET /api/events` - List all events
- `GET /api/events/{id}` - Get event details
- `POST /api/events` - Create new event (ADMIN/ORGANIZER)
- `PUT /api/events/{id}` - Update event (ADMIN/ORGANIZER)
- `DELETE /api/events/{id}` - Delete event (ADMIN)

### Tickets
- `POST /api/tickets/purchase` - Purchase tickets
- `GET /api/tickets/{id}/qr` - Get ticket QR code
- `POST /api/tickets/{id}/validate` - Validate ticket
- `GET /api/tickets/user` - Get user's tickets

### Analytics
- `GET /api/analytics/event/{id}` - Event statistics
- `GET /api/analytics/dashboard` - Dashboard metrics

## Configuration

### Application Properties

Key configurations in `application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database (H2)
spring.datasource.url=jdbc:h2:file:./data/ticketing-db
spring.datasource.username=sa
spring.datasource.password=password

# JWT
jwt.secret=[your-secret-key]
jwt.expiration=3600000
jwt.refresh.expiration=604800000

# OAuth2 (configure for production)
spring.security.oauth2.client.registration.google.client-id=[your-google-client-id]
spring.security.oauth2.client.registration.facebook.client-id=[your-facebook-client-id]
```

### Environment Variables

For production, set these environment variables:
- `DATABASE_URL` - PostgreSQL connection URL
- `JWT_SECRET` - Secret key for JWT tokens
- `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`
- `FACEBOOK_CLIENT_ID` and `FACEBOOK_CLIENT_SECRET`

## Building for Production

1. **Build JAR file**
   ```bash
   ./mvnw clean package
   ```

2. **Run JAR**
   ```bash
   java -jar target/ticketing-1.0.0.jar
   ```

3. **Docker deployment**
   ```bash
   docker build -t ticketing-backend .
   docker run -p 8080:8080 ticketing-backend
   ```

## Database

The application uses H2 file-based database by default, storing data in `./data/ticketing-db.mv.db`. 

For production, configure PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## Security

- JWT tokens expire after 1 hour
- Refresh tokens expire after 7 days
- Passwords are encrypted using BCrypt
- CORS is configured for frontend integration
- Default admin credentials should be changed in production

## Development

### Running Tests
```bash
./mvnw test
```

### Hot Reload
Spring DevTools is included for automatic restart during development.

### Sample Data
The application loads sample data on startup (configurable in `DataLoader.java`).

## Project Structure

```
backend/
├── src/main/java/com/system/ticketing/
│   ├── config/         # Configuration classes
│   ├── controller/     # REST controllers
│   ├── dto/           # Data Transfer Objects
│   ├── entity/        # JPA entities
│   ├── repository/    # Data repositories
│   ├── service/       # Business logic
│   └── security/      # Security configuration
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/  # Database migrations
└── pom.xml
```

## License

[Your License]

## Support

For issues and questions, please open an issue in the repository.# backend
