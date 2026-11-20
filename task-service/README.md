## Task Service

**Port** 8082 (reached through the gateway) · **Database** PostgreSQL `taskdb`

Task Service is where the actual work of a renovation is planned. A homeowner splits a project into tasks, a contractor gets assigned to each one,
and as the contractor moves a task from `PLANNED` to `COMPLETED`, the homeowner can see how far the project has come.
I wanted this service to answer a simple question: "how is the renovation going?"

It is the only service that stores task data. Other services refer to a task by `taskId` and to its project by `projectId`.

## Endpoints

All requests go through the gateway: `http://localhost:8080`.

| Method | Path | Description                                                                       |
|---|---|-----------------------------------------------------------------------------------|
| `POST` | `/api/tasks` | Create a task for a project (starts as `PLANNED`) → `201`                         |
| `GET` | `/api/tasks` | List tasks (optional `projectId=5` or `contractorId=3`)                           |
| `GET` | `/api/tasks/{id}` | Get one task (`404` if it does not exist)                                         |
| `PATCH` | `/api/tasks/{id}/contractor?contractorId=3` | Assign a contractor to a task                                                     |
| `PATCH` | `/api/tasks/{id}/status?status=COMPLETED&contractorId=3` | Update the status. Only the assigned contractor may do this → `403` for anyone else |
| `GET` | `/api/tasks/project/{projectId}/progress` | How many tasks are in each status, and the percentage completed                   |

If something is wrong with the input, for example a missing `projectId` or `title`, the service answers `400` with a message that says what is missing. 
You can also try everything in Swagger directly on the service: `http://localhost:8082/swagger-ui.html`.

### How it communicates with other services

- **Kafka, `task-events`:** every time a task changes status, I publish a small JSON event with `projectId` as the key, so events for the same project stay in order. The type is `TaskCompleted` when a task is finished, otherwise `TaskStatusChanged`. Other services, such as notification-service, can listen to it without task-service knowing about them.
  ```json
  { "eventType": "TaskCompleted", "taskId": 1, "projectId": 5, "newStatus": "COMPLETED", "contractorId": 3, "timestamp": "2026-11-20T12:00:00Z" }
  ```
  If Kafka is down, the status is still saved. The error is logged, but the contractor is not blocked.
- **REST, Task → Project (optional):** with `PROJECT_VALIDATION_ENABLED=true`, task-service asks project-service (`GET /api/projects/{id}`) whether the project exists before it creates a task. A project that does not exist gives `400`, and if project-service is down the answer is `503`. I left it off by default so the service can be started and tested on its own.
- **Consul:** the service registers itself in Consul, which also checks `/actuator/health`. The gateway finds it with `lb://task-service`.

1. `POST /api/tasks` `{ "projectId": 5, "title": "Living room", "description": "Tear down wall", "deadline": "2026-12-01" }` → `201`, status `PLANNED`
2. `PATCH /api/tasks/1/contractor?contractorId=3` → the task now belongs to contractor 3
3. `PATCH /api/tasks/1/status?status=COMPLETED&contractorId=3` → `200`, and a `TaskCompleted` event is sent to Kafka
4. `PATCH /api/tasks/1/status?status=IN_PROGRESS&contractorId=7` → `403`, because contractor 7 is not assigned to this task
5. `GET /api/tasks?contractorId=3` → everything contractor 3 is working on
6. `GET /api/tasks/project/5/progress` → for example `total: 4, planned: 3, completed: 1, percentageComplete: 25`
7. `GET /api/tasks/9999` → `404`

Settings are environment variables with sensible defaults: `SERVER_PORT` (8082), `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS` (`kafka:9093` in Docker), `TASK_TOPIC` (`task-events`), `CONSUL_ENABLED`, `PROJECT_VALIDATION_ENABLED` (false) and `PROJECT_SERVICE_URL` (`http://project-service:8085` in Docker).

### Decisions and simplifications
- **Tasks live in one place.** Only task-service stores them. Other services keep an id, never a copy, so the data cannot drift apart.
- **Only the assigned contractor can change the status.** There is no login, so the contractor's id is sent in the request. 
- That means anyone can pretend to be a contractor, and the id is not checked against user-service. It was a conscious shortcut.
- **The schema is created by Hibernate** (`ddl-auto: update`) instead of migrations, to keep the setup simple.
- **The event is sent after the task is saved, not in the same transaction.** If Kafka is down, that event is lost.
- A production system would use the outbox pattern, and that is the first thing I would add with more time.
- **No test data is seeded.** Create a few tasks through the gateway, as shown above, and the progress endpoint starts to show something.