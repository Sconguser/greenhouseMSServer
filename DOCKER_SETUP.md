# Running the Greenhouse Server with Docker

**Prerequisites:** Docker Desktop (Windows), Git

---

## Quick start (5 steps)

### 1 — Clone the repository

```bash
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

### 2 — Create your `.env` file

```bash
copy .env.example .env       # Windows CMD
# or
cp .env.example .env         # Git Bash / PowerShell
```

Open `.env` in any text editor and fill in the two required values:

| Variable | What to put |
|---|---|
| `POSTGRES_PASSWORD` | Any password you choose (e.g. `mypassword123`) |
| `JWT_SECRET_KEY` | A random string ≥ 32 characters — use a password manager or [this generator](https://1password.com/password-generator/) |

Everything else can stay as-is for local use.

### 3 — Add the Firebase credentials file

Push notifications (FCM) require a Firebase service account key.
**Get `firebase.json` from the project owner** (it is not committed to git for security reasons).

Place it at:
```
secrets/firebase.json
```

> **No Firebase file?** Copy the placeholder and the server will still start — push notifications just won't work:
> ```bash
> copy secrets\firebase.json.example secrets\firebase.json
> ```

### 4 — Start the stack

```bash
docker compose up --build
```

Docker will:
1. Build the Spring Boot server from source (takes ~3 min on first run; subsequent runs are fast due to layer caching)
2. Start PostgreSQL, create the `greenhouse` database, and run schema migration automatically
3. Start the Mosquitto MQTT broker
4. Start the Spring Boot server on port `8080`

You should see `Started GreenhouseApplication` in the logs when it's ready.

### 5 — Connect the Flutter app

In the Flutter app, set the server address to:
```
http://<this-machine's-ip>:8080
```

If running everything on the same machine, use `http://localhost:8080`.

To find your machine's IP on Windows:
```bash
ipconfig
# Look for "IPv4 Address" under your active network adapter
```

---

## Useful commands

```bash
# Start in background (detached)
docker compose up -d --build

# View logs
docker compose logs -f app

# Stop everything
docker compose down

# Stop and delete the database volume (full reset)
docker compose down -v

# Restart just the server after a code change
docker compose up -d --build app
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `Error: JWT_SECRET_KEY must not be null` | Make sure `.env` exists and `JWT_SECRET_KEY` is set |
| `Connection refused` on port 5432 | PostgreSQL container is still starting — wait a few seconds and retry |
| `Firebase initialization failed` | The `secrets/firebase.json` is a placeholder; get the real file from the project owner |
| App connects but gets `401 Unauthorized` | The JWT secret in `.env` doesn't match the one used to generate the token — re-login in the app |
| Docker build fails on Maven download | Slow internet; the build will retry automatically up to 5 times (configured in `mvn-settings/settings.xml`) |

---

## Architecture overview

```
[Flutter app]  ──HTTP──▶  [Spring Boot :8080]  ──JDBC──▶  [PostgreSQL :5432]
                                  │
                               MQTT
                                  │
                          [Mosquitto :1883]  ◀──── [ESP8266 board]
```

All three server containers (`app`, `db`, `mqtt`) run inside an isolated Docker network and communicate using their service names. Only ports `8080` and `1883` are exposed to the host.