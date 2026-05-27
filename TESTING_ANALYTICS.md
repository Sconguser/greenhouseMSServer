# Testing Analytics Without a Board

Two independent methods — use either or both.

---

## Method 1 — Seed endpoint (fastest, bulk data)

Writes synthetic parameter history and lifecycle events **directly into the
database**, bypassing MQTT entirely.  The generated values oscillate smoothly
between each parameter's configured `min`/`max`, so charts look realistic.

### Seed data

```http
POST http://localhost:8080/analytics/dev/seed
Authorization: Bearer <your-jwt>
```

With query parameters (all optional):

| Parameter         | Default | Meaning                                       |
|-------------------|---------|-----------------------------------------------|
| `greenhouseId`    | all     | Seed only this greenhouse                     |
| `hours`           | 24      | How far back the history starts (1–8760)      |
| `intervalMinutes` | 10      | Gap between generated readings (1–1440)       |

#### Examples

```bash
# Seed all greenhouses with 24 h of data, one point every 10 min
curl -X POST "http://localhost:8080/analytics/dev/seed" \
     -H "Authorization: Bearer $TOKEN"

# Seed greenhouse 1 with 7 days of data, one point every 30 min
curl -X POST "http://localhost:8080/analytics/dev/seed?greenhouseId=1&hours=168&intervalMinutes=30" \
     -H "Authorization: Bearer $TOKEN"

# Seed 1 hour at maximum resolution (1 point per minute)
curl -X POST "http://localhost:8080/analytics/dev/seed?hours=1&intervalMinutes=1" \
     -H "Authorization: Bearer $TOKEN"
```

#### Sample response

```json
{
  "totalHistoryRowsInserted": 864,
  "totalEventsInserted": 6,
  "greenhouses": [
    {
      "greenhouse": "Greenhouse A",
      "parameters": ["Temperature", "Humidity", "Soil Moisture"],
      "historyRowsInserted": 864,
      "eventsInserted": 6
    }
  ]
}
```

Events generated follow a realistic sequence:
- **BOOT** at the start of the window
- 1–3 **CRASH → BOOT** pairs spread through the window
- All timestamps are deterministic for the same greenhouse ID

### Clear all analytics data (reset between test runs)

```bash
curl -X DELETE "http://localhost:8080/analytics/dev/seed" \
     -H "Authorization: Bearer $TOKEN"
```

---

## Method 2 — MQTT simulation (exercises the full pipeline)

Uses the existing `/mqtt/debug/publish` endpoint to inject fake telemetry as
if the board were sending it.  This exercises `updateTelemetry()` →
`recordTelemetryReadings()` and also triggers BOOT detection when you simulate
a device going offline and coming back.

### Step 1 — find your IDs

```bash
curl http://localhost:8080/greenhouse/ -H "Authorization: Bearer $TOKEN"
```

Note the greenhouse `id`, zone `id`s, flowerpot `id`s, and parameter `id`s.

### Step 2 — publish a telemetry message

```http
POST http://localhost:8080/mqtt/debug/publish
Authorization: Bearer <your-jwt>
Content-Type: application/json

{
  "topic": "greenhouse/<ip>/status",
  "payload": "{\"id\":<greenhouse_id>,\"zones\":[{\"id\":<zone_id>,\"parameters\":[{\"id\":<param_id>,\"val\":22.5}],\"flowerpots\":[{\"id\":<flowerpot_id>,\"parameters\":[{\"id\":<fp_param_id>,\"val\":65.0}]}]}]}"
}
```

Replace `<ip>` with the `ipAddress` stored in the greenhouse row (e.g. `192.168.1.100`).

#### Minimal working example (greenhouse 1, zone 1, one parameter id=3 at 22.5)

```bash
curl -X POST http://localhost:8080/mqtt/debug/publish \
     -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "topic": "greenhouse/192.168.1.100/status",
       "payload": "{\"id\":1,\"zones\":[{\"id\":1,\"parameters\":[{\"id\":3,\"val\":22.5}],\"flowerpots\":[]}]}"
     }'
```

Each call produces **one row** in `parameter_history`.  Run it in a loop to
generate a time series (values changing between calls):

```bash
# Bash: send 30 readings with a random temperature value, 1 second apart
for i in $(seq 1 30); do
  TEMP=$(python3 -c "import random; print(round(18 + random.random()*10, 1))")
  curl -s -X POST http://localhost:8080/mqtt/debug/publish \
       -H "Authorization: Bearer $TOKEN" \
       -H "Content-Type: application/json" \
       -d "{\"topic\":\"greenhouse/192.168.1.100/status\",\"payload\":\"{\\\"id\\\":1,\\\"zones\\\":[{\\\"id\\\":1,\\\"parameters\\\":[{\\\"id\\\":3,\\\"val\\\":$TEMP}],\\\"flowerpots\\\":[]}]}\"}"
  echo "Sent temp=$TEMP"
  sleep 1
done
```

### Step 3 — simulate a crash and recovery (tests BOOT/CRASH events)

A crash event fires when the status scheduler hasn't heard from a greenhouse
for > 5 minutes.  To avoid waiting, you can just use the seed endpoint's
generated events, or:

1. Send one telemetry message (so the greenhouse is `ON`).
2. Wait 6 minutes without sending anything.
3. The `GreenhouseStatusScheduler` marks it `NOT_RESPONSIVE` → a **CRASH** event is recorded.
4. Send another telemetry message → the server detects `wasOffline=true` → a **BOOT** event is recorded.

---

## Verifying the data

### Check the database directly

```sql
-- How many history rows do we have?
SELECT COUNT(*) FROM parameter_history;

-- Last 10 readings for a parameter
SELECT recorded_at, value FROM parameter_history
WHERE parameter_id = 3
ORDER BY recorded_at DESC
LIMIT 10;

-- All events
SELECT e.occurred_at, e.event_type, g.name
FROM greenhouse_event e
JOIN greenhouse g ON g.id = e.greenhouse_id
ORDER BY e.occurred_at DESC;
```

### Check via the REST API

```bash
# Parameter history (last 24 h, parameter id=3)
curl "http://localhost:8080/analytics/parameter/3/history?from=$(date -d '24 hours ago' +%Y-%m-%dT%H:%M:%S)&to=$(date +%Y-%m-%dT%H:%M:%S)" \
     -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

# Events (last 7 days, greenhouse 1)
curl "http://localhost:8080/analytics/greenhouse/1/events?from=$(date -d '7 days ago' +%Y-%m-%dT%H:%M:%S)&to=$(date +%Y-%m-%dT%H:%M:%S)" \
     -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

# Stats
curl "http://localhost:8080/analytics/greenhouse/1/stats?from=$(date -d '7 days ago' +%Y-%m-%dT%H:%M:%S)&to=$(date +%Y-%m-%dT%H:%M:%S)" \
     -H "Authorization: Bearer $TOKEN"
```

---

## Removing the seed endpoint before production

Delete or disable `AnalyticsSeedController.java`, or add a profile guard:

```java
@Profile("dev")   // add this annotation to the class
@RestController
@RequestMapping("/analytics/dev")
public class AnalyticsSeedController { ... }
```

Then start the server with `--spring.profiles.active=prod` to exclude it.