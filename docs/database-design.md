# WastePickup Database Design

Database: `WastePickup` (MySQL)

## Tables

| Table | Purpose | Primary Key | Main Foreign Keys |
|---|---|---|---|
| `zones` | Collection areas | `id` | — |
| `schedules` | One fixed pickup window per zone | `id` | `zone_id -> zones.id` |
| `households` | Households assigned to a zone | `id` | `zone_id -> zones.id` |
| `pickup_logs` | Collector pickup event + score | `id` | `household_id`, `schedule_id` |

## Relationships

- Zone 1 : 0..1 Schedule
- Zone 1 : N Household
- Household 1 : N PickupLog
- Schedule 1 : N PickupLog

## Derived business data

`households.average_score` stores the latest rolling average so the dashboard can read it quickly.
The source history remains in `pickup_logs`.

The rolling window is configurable in `application.properties`:

```properties
app.scoring.rolling-pickups=5
app.scoring.minimum-average=60
```

A household is flagged immediately after a pickup when its rolling average is below the configured minimum.
