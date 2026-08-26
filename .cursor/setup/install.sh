#!/usr/bin/env bash
# Idempotent repository bootstrap for the darkme2 Cloud Agent environment.
# Ensures Java 8 is active and builds the runnable Spring Boot fat jar.
#
# The project compiles at source/target 8 and uses the compiler `extdirs`
# option (removed in JDK 9+), so it MUST be built with JDK 8. JDK 8 and MySQL
# are provided by the environment's base snapshot; the guards below re-install
# them only if a plain base image is ever used, keeping this script safe to run
# anywhere.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

JAVA8_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Self-heal: install JDK 8 / MySQL if the base image lacks them.
if [ ! -x "$JAVA8_HOME/bin/javac" ]; then
  sudo apt-get update -qq
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq openjdk-8-jdk
fi
if ! command -v mysqld >/dev/null 2>&1 && [ ! -x /usr/sbin/mysqld ]; then
  sudo apt-get update -qq
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mysql-server
fi

# Make JDK 8 the active java/javac (idempotent).
sudo update-alternatives --set java  "$JAVA8_HOME/jre/bin/java"  >/dev/null 2>&1 || true
sudo update-alternatives --set javac "$JAVA8_HOME/bin/javac"     >/dev/null 2>&1 || true
export JAVA_HOME="$JAVA8_HOME"

echo "Building darkme2 with $(java -version 2>&1 | head -1)"
./mvnw -q -DskipTests clean package

echo "Build complete: $(ls -1 target/darkme2-*.jar 2>/dev/null | head -1)"
