Notification Service

## What is Notification Service for?
Notification Service helps in managing notifications for users in Home Renovation Management Program.
It works by 'creating' notifications for users, 'obtain' notifications that belongs to a specific user. It
can delete notifications as well as store notification in PostgreSQL database.

## What Java version and dependencies were used?
For notification service, Java version 21, Spring Boot, Spring Data JPA, PostgreSQL, Docker, Maven we used

## API Endpoints

POST  -> /api/notifications   -> creates a notification
GET  ->  /api/notification/user/{id}  -> gets a users notification by their ID
DELETE  -> /api/notifications/{id}  -> deletes a notification by using id

## To build a Docker image
docker build -t notification-service:local .

## To run
docker run -d --name notification-service-container --network notification-service-network -p 8083:8083 notification-service:local

## Where server runs?
http://localhost:8083

## To run test
.mvnw.cmd test

## Health Endpoint
Health endpoint: http://localhost:8083/actuator/health