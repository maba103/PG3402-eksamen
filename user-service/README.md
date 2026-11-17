User Service

## What is User Service about?
Our project is about managing home renovations. User service helps in managing users in the application which includes
homeowners and contractors. User Service allows to create, obtain, update and delete user information.

## What Java version and dependencies were used?
For user service, we used Java version 21, Spring Boot, Spring Data JPA, PostgreSQL, Docker, Maven, Swagger,
BCrypt password hashing and JUnit.

## Types of users in the service
User service has two types of users: Homeowners and Contractor. They both have
different responsibilities for the system and project. Homeowner focuses on managing the budget while contractor can 
update the progress of the project.

## API Endpoints
URL: http://localhost:8082

There are different kinds of methods, each with their endpoints. Such as:

GET  ->  /api/users  -> gets data for all users
GET  ->  /api/users/{id}  -> gets users by their given id
POST  -> /api/users   -> creates a new user
PUT   -> /api/users/{id}  -> updates the user data
DELETE  -> /api/users/{id}  -> deletes user by id

## An example
In order to create a user, we use POST (POST  -> /api/users   -> creates a new user)

{
 "name": "User1",
 "email": "user1@mail.com",
 "role": "HOMEOWNER",
 "password": "Password123"
}

Passwords are safely protected and hidden. It also prevents an email from being repeated. "409 Conflict" error is shown.

## To build a Docker image
docker build -t user-service:local .

## To create a Docker network
docker network create user-service-network

## To start PostgreSQL
docker run -d --name user-service-service-db --network -user-service-network -e POSTGRES_DB=userdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16

## To start the service
docker run -d --name user-service-container --network user-service-network -p 8082:8081 user-service:local

## To test
.\mvnw.cmd test

## Swagger UI and Health Endpoint
Swagger UI: http://localhost:8082/swaggerui/index.html
Health endpoint: http://localhost:8082/actuator/health