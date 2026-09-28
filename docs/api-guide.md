# WastePickup REST API

Base URL: `http://localhost:8080/api`

Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI JSON: `http://localhost:8080/api-docs`
Web dashboard: `http://localhost:8080/`

## Zones

- `GET /zones`
- `GET /zones/{id}`
- `POST /zones`
- `PUT /zones/{id}`
- `DELETE /zones/{id}`

POST example:
```json
{"name":"North Zone","code":"Z01","active":true}
```

## Schedules

- `GET /schedules`
- `GET /schedules/{id}`
- `POST /schedules`
- `PUT /schedules/{id}`
- `DELETE /schedules/{id}`

POST example:
```json
{"zoneId":1,"pickupDay":"MONDAY","startTime":"08:00:00","endTime":"11:00:00"}
```

## Households

- `GET /households`
- `GET /households?zoneId=1`
- `GET /households?flagged=true`
- `GET /households/{id}`
- `POST /households`
- `PUT /households/{id}`
- `DELETE /households/{id}`

POST example:
```json
{"houseNumber":"101","residentName":"Sanjay","phone":"9876500001","zoneId":1}
```

## Pickup logs

- `GET /pickups`
- `GET /pickups/{id}`
- `GET /pickups/household/{householdId}`
- `GET /pickups/household/{householdId}/average`
- `POST /pickups`
- `PUT /pickups/{id}`
- `DELETE /pickups/{id}`
- `GET /pickups/reports/zone-scores`

POST example:
```json
{
  "householdId":1,
  "collectorName":"Collector A",
  "pickupTime":"2026-09-28T09:30:00",
  "segregationScore":82
}
```

If the timestamp is outside the household zone's configured pickup day/time window, the service returns HTTP 400 with a clear business-rule message.
