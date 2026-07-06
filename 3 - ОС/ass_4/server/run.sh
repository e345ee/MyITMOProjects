#!/usr/bin/env bash
set -euo pipefail
JDBC_JAR="${JDBC_JAR:-postgresql.jar}"
mkdir -p out
javac -d out src/VtfsHttpServer.java
exec java -cp "out:${JDBC_JAR}" VtfsHttpServer
