# WastePickup — Waste Collection Schedule and Segregation Score Tracker

A Spring Boot + MySQL integrated project based on the supplied reference CRUD structure, expanded into a layered REST application.

## Technology

- Java 17+
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- MySQL
- Springdoc OpenAPI / Swagger UI
- HTML + CSS + JavaScript frontend

## Architecture

```text
Frontend (HTML/CSS/JS)
        |
        v
REST Controllers
        |
        v
Service Layer  <-- business rules
        |
        v
Repository Layer
        |
        v
JPA Entities / MySQL
```

## Main business rules

1. A zone has one configured pickup schedule.
2. A pickup log can be recorded only when its pickup timestamp matches the zone schedule day and is inside the start/end window.
3. Segregation score must be 0–100.
4. After each pickup, the rolling average of the last configurable number of scores is calculated.
5. If the rolling average is below the configured minimum, the household is automatically flagged for a reminder.
6. Zone-wise average score is available as a report endpoint and on the web dashboard.

## Setup in VS Code

### 1. MySQL

Run:

```sql
CREATE DATABASE WastePickup;
```

Open `src/main/resources/application.properties` and change the password if your MySQL root password is not `Root`:

```properties
spring.datasource.username=root
spring.datasource.password=Root
```

### 2. Open project

Open the extracted `WastePickup` folder in VS Code.

### 3. Run

Windows:

```powershell
mvn spring-boot:run
```

or, if Maven is not installed:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Open

- Web: http://localhost:8080/
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Suggested demo sequence

1. Create a zone.
2. Create its schedule.
3. Create two households in that zone.
4. Record a pickup during the scheduled window.
5. Repeat pickups with scores such as 90, 75, 50, 45, 40 to demonstrate rolling-average flagging.
6. Open the zone report and web dashboard.
7. Try a pickup outside the scheduled window to demonstrate the HTTP 400 business-rule response.

## Reference structure

The supplied `demo1.zip` was used as the structural reference: Spring Boot application, controller/model/repository/service layers, MySQL configuration, and Swagger. The duplicate Swagger dependency and generic `AppModel` were replaced with a project-specific layered design so the generated project avoids the duplicate-dependency warning seen in the reference.
