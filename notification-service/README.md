Notification Service

## What is Notification Service for?
Notification Service helps to manage notifications for users in Home Renovation Management Program.
It works by fulfilling CRUD operations. Users can create, retrieve and delete notifications . It can also retrieve 
notifications that belongs to a specific user by using "/api/notification/user/{userId}". All notifications are stored
in PostgreSQL database.

Notification Service also receives budget events from Budget Service through Apache Kafka. When a budget warning occurs, 
the service receives a message and saves the notification in the database (PostgreSQL).

## What Java version and dependencies were used?
For notification service: Java version 21, Spring Boot, Spring Data JPA, PostgreSQL, Apache Kafka, Spring Kafka, 
Docker, Maven and Consul were used

## API Endpoints

POST  -> /api/notifications   -> creates a notification
GET  -> /api/notifications    -> obtains all notifications 
GET  ->  /api/notifications/user/{userId}  -> gets a users notification by ID
DELETE  -> /api/notifications/{id}  -> deletes a notification by id

## Database
PostgreSQL database -> notificationdb
Container -> notification-service-db
Internal port -> 5432
Host port -> 5434

## To start Docker application
Notification service is included in docker-compose.yml . 
Application starts:
docker compose up -d --build 

For notification logs:
docker compose logs notification-service

## Where server runs?
http://localhost:8083
through API gateways -> http://localhost:8080/api/notifications

## Consul
http://localhost:8500

## To run test
.mvnw.cmd test

## Health Endpoint
Health endpoint: http://localhost:8083/actuator/health