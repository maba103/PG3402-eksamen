## Project Service

**Owner** 57 · **Port** 8005 (reached through the gateway) · **Database** PostgreSQL `projetdb`

Manages renovation projects, and shows a summary of each project together with its budget. It is the only service that 
stores project data; other services refer to projects by projectId.

## Endpoints

All requests go through the gateway: `http://localhost:8080`. 

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/projects` | Create a project → `201` |
| `GET` | `/api/projects` | List projects (optional `?ownerId=1`) |
| `GET` | `/api/projects/{id}` | Get one project (`404` if it does not exist) |
| `PUT` | `/api/projects/{id}` | Update a project |
| `DELETE` | `/api/projects/{id}` | Delete a project and publish `PROJECT_DELETED` → `204` |
| `GET` | `/api/projects/{id}/summary` | The project together with its budget from Budget Service |

Invalid input (missing name or owner, end date before start date) returns `400` with a clear message.

### Communication 

- **REST, Project -> Budget** `/summary` fetches the budget from Budget Service. If Budget Service is down, it still returns the project with `budgetAvailability: SERVICE_UNAVAILABLE` instead of failing. REST, Budget -> Project: ** Budget Service checks that a project exists before creating a budget. 
- **REST, Budget → Project:** Budget Service checks that a project exists before creating a budget.
- **Kafka, `project-events`:** deleting a project publishes `PROJECT_DELETED`, and Budget Service deletes that project`s 
budget.

### Test data and user stories

Seeded on startup: **1** Bathroom renovation, **2** Kitchen renovation (both with budgets in Budget Service), **3** Terrace (no budget).

1. `GET /api/projects/1/summary` -> project with its budget (`AVAILABLE`).
2. `GET /api/projects/3/summary` -> `NOT_REGISTERED`, the project has no budget yet
3. `POST /api/budgets` with `"projectId": 999 -> `404`, because Budget Service asked Project Service.
4. `docker compose` stop budget-service, then `GET /api/projects/1/summary` -> still `200, with `SERVICE_UNAVAILABLE`.
5. `DELETE /api/projects/2`, then `GET /api/budgets/project/2` -> `404`, the budget was removed through Kafka.
6. `docker compose up -d --scale project-service=2` -> two instances in Consul, load-balanced by the gateway. 

### Decisions and simplifications
- **Budget Service owns the budget amount.** Project Service stored its own `budgetLimit`, which duplicated data; it was removed, so there is no source of truth.
- **`ownerId` is sent in the request** instead of coming from a login.
- **The schema is created by Hibernate** (`ddl-auto: update`), instead of migrations.
- **The event is published inside the delete transaction**; a production system would use the outbox pattern.
