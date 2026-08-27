#!/usr/bin/env bash
# Runs the darkme2 Spring Boot app against the local MySQL database.
#
# The app serves HTTPS on :30001 (self-signed cert from the bundled keystore)
# and an HTTP :80 connector that redirects to HTTPS. Binding :80 requires root,
# so the jar is launched with sudo. The committed jdbc.properties points at a
# dead remote MySQL, so the local datasource is supplied via command-line
# overrides (DataSourceConfig reads these @Value properties).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

JAR="$(ls -1 target/darkme2-*.jar 2>/dev/null | head -1 || true)"
if [ -z "$JAR" ]; then
  echo "Fat jar not found; building it first..."
  ./mvnw -q -DskipTests package
  JAR="$(ls -1 target/darkme2-*.jar | head -1)"
fi

DB_URL='jdbc:mysql://127.0.0.1:3306/darkme?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true'

echo "Launching $JAR (HTTPS https://localhost:30001/ , HTTP :80 -> HTTPS redirect)"
exec sudo JAVA_HOME="$JAVA_HOME" "$JAVA_HOME/bin/java" -jar "$JAR" \
  --spring.datasource.url="$DB_URL" \
  --spring.datasource.username=darkme \
  --spring.datasource.password=darkme_dev_pw
