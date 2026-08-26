#!/usr/bin/env bash
# Per-boot startup for the darkme2 Cloud Agent environment.
#   1. Start local MySQL and ensure the `darkme` schema exists (idempotent).
#   2. Launch the Spring Boot app in the background (idempotent, no duplicates).
#   3. Wait until HTTPS is serving, then return.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
APP_LOG=/tmp/darkme-app.log

echo "Starting MySQL..."
sudo service mysql start || true

for i in $(seq 1 30); do
  sudo mysqladmin ping >/dev/null 2>&1 && break
  sleep 1
done
if ! sudo mysqladmin ping >/dev/null 2>&1; then
  echo "MySQL did not become ready in time" >&2
  exit 1
fi

echo "Applying database schema (idempotent)..."
sudo mysql < "$REPO_ROOT/.cursor/setup/schema.sql"
echo "MySQL is ready; darkme schema ensured."

# If the app already answers on HTTPS, leave it running.
if curl -sk -o /dev/null --max-time 5 https://localhost:30001/ ; then
  echo "darkme2 already running on https://localhost:30001/"
  exit 0
fi

# Clear any stale listeners on the app ports to avoid "address already in use".
for port in 30001 80; do
  pid="$(sudo lsof -t -i:"$port" 2>/dev/null || true)"
  [ -n "$pid" ] && sudo kill $pid 2>/dev/null || true
done
sleep 1

echo "Launching darkme2 in the background (logs: $APP_LOG)..."
nohup "$REPO_ROOT/.cursor/setup/run-app.sh" > "$APP_LOG" 2>&1 &

# Wait for HTTPS readiness.
for i in $(seq 1 60); do
  if curl -sk -o /dev/null --max-time 3 https://localhost:30001/ ; then
    echo "darkme2 is up: https://localhost:30001/ (HTTP :80 redirects to HTTPS)"
    exit 0
  fi
  sleep 1
done

echo "darkme2 did not become ready in time; see $APP_LOG" >&2
tail -n 30 "$APP_LOG" >&2 || true
exit 1
