#!/usr/bin/env bash
# Per-boot startup for the darkme2 Cloud Agent environment.
# Starts the local MySQL server and ensures the `darkme` schema exists.
# Tolerates an already-running server and returns once MySQL is reachable.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "Starting MySQL..."
sudo service mysql start || true

# Wait until MySQL accepts connections.
for i in $(seq 1 30); do
  if sudo mysqladmin ping >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

if ! sudo mysqladmin ping >/dev/null 2>&1; then
  echo "MySQL did not become ready in time" >&2
  exit 1
fi

echo "Applying database schema (idempotent)..."
sudo mysql < "$REPO_ROOT/.cursor/setup/schema.sql"

echo "MySQL is ready; darkme schema ensured."
