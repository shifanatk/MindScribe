# MindScribe - Running Backend & Frontend Separately

## Prerequisites
- Java 17+
- Maven 3.6+
- MongoDB Atlas (for user/profile data)

## Quick Start

### 1. Run Backend (Spring Boot)
```bash
mvn spring-boot:run -Pbackend
```
- Backend will start on `http://localhost:8080`
- H2 Console available at `http://localhost:8080/h2-console`
- MongoDB Atlas for user data
- H2 database for journal entries

### 2. Run Frontend (JavaFX GUI)
In a separate terminal:
```bash
mvn javafx:run -Pfrontend
```
- JavaFX GUI application will start
- Requires backend to be running for full functionality

### 3. Integration Check
- Backend: Check `http://localhost:8080` (should return 401 - normal for secured app)
- Frontend: GUI should open and show login screen
- Both services can run simultaneously in different terminals

## Maven Profiles

### Backend Profile (`-Pbackend`)
- Runs Spring Boot application
- Main class: `com.mindscribe.MindscribeBackendApplication`
- Port: 8080

### Frontend Profile (`-Pfrontend`)
- Runs JavaFX GUI application
- Main class: `com.mindscribe.GUILauncher`
- Requires JavaFX dependencies

## Development Commands

### Clean and Compile
```bash
# Backend
mvn clean compile -Pbackend

# Frontend  
mvn clean compile -Pfrontend
```

### Run Tests
```bash
mvn test
```

### Package
```bash
mvn package -Pbackend
mvn package -Pfrontend
```

## Architecture
- **Backend**: Spring Boot with Security, MongoDB, H2
- **Frontend**: JavaFX with FXML
- **Communication**: REST API calls from frontend to backend
- **Databases**: MongoDB Atlas (users), H2 file (journal entries)

## Troubleshooting

### Backend Issues
- Check MongoDB connection in `application.properties`
- Verify port 8080 is not in use
- Check H2 console access

### Frontend Issues
- Ensure Java 17+ is installed
- Verify JavaFX dependencies are resolved
- Backend must be running for full functionality

### Integration Issues
- Both services should run simultaneously
- Frontend makes HTTP calls to `localhost:8080`
- Check network connectivity between services
