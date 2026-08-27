#!/usr/bin/env bash
# Per-boot startup for the darkme2 Cloud Agent environment.
#   1. Start local MySQL and ensure the `darkme` schema exists (idempotent).
#   2. Launch the Spring Boot app in the background (idempotent, no duplicates).
#   3. Wait until HTTPS is serving, then return.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
APP_LOG=/tmp/darkme-app.log

wait_mysql() {
  for _ in $(seq 1 "$1"); do
    sudo mysqladmin ping >/dev/null 2>&1 && return 0
    sleep 1
  done
  return 1
}

echo "Starting MySQL..."
sudo service mysql start || true

if ! wait_mysql 15; then
  # On a freshly booted pod the MySQL datadir comes from the environment
  # image's overlayfs *lower* layer, where InnoDB's O_DIRECT probe on the redo
  # log returns EINVAL and aborts startup. Copy the datadir up into the
  # writable layer (only when the first start failed) and retry.
  echo "MySQL did not come up; applying overlayfs datadir copy-up workaround..."
  sudo service mysql stop >/dev/null 2>&1 || true
  if sudo test -d /var/lib/mysql; then
    sudo rm -rf /var/lib/mysql.old /var/lib/mysql.new
    sudo cp -a /var/lib/mysql /var/lib/mysql.new
    sudo mv /var/lib/mysql /var/lib/mysql.old
    sudo mv /var/lib/mysql.new /var/lib/mysql
    sudo chown -R mysql:mysql /var/lib/mysql
    sudo rm -rf /var/lib/mysql.old
  fi
  sudo service mysql start || true
  if ! wait_mysql 30; then
    echo "MySQL did not become ready in time" >&2
    exit 1
  fi
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
