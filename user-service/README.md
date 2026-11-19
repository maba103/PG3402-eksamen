User Service

## What is User Service about?
Our project is about managing home renovations. User service helps in managing users in the application including
homeowners and contractors. User Service allows to create, obtain, update and delete user information.

## What Java version and dependencies were used?
For user service, Java version 21, Spring Boot, Spring Data JPA, PostgreSQL, Docker, Maven, Swagger,
BCrypt password hashing and JUnit were used.

## Types of users in the service
User service has two types of users: Homeowners and Contractor. They both have different responsibilities for the system and project. 
Homeowner focuses on managing the budget while contractor helps in updating the progress of the project.

## API Endpoints
URL: http://localhost:8081

GET  ->  /api/users  -> gets data for all users
GET  ->  /api/users/{id}  -> gets users by their given id
GET  -> /api/users/role/{role} -> gets users by their role
POST  -> /api/users   -> creates a new user
PUT   -> /api/users/{id}  -> updates the existing user data
DELETE  -> /api/users/{id}  -> deletes user by id

Passwords are safely protected and hidden. It also prevents an email from being repeated. "409 Conflict" error is shown.

## Docker 
User Service is included in the root docker-compose.yml 

To start application: docker compose up -d --build

User Service logs : docker compose logs user-service

Database: userdb
Container: user-service-db
Internal port: 5432
Host port: 5435

## To test
.\mvnw.cmd test

## Swagger UI and Health Endpoint
Swagger UI: http://localhost:8081/swaggerui/index.html
Health endpoint: http://localhost:8081/actuator/health