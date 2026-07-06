#!/usr/bin/env bash
set -euo pipefail
PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-6432}"
PGUSER="${PGUSER:-appuser}"
PGDATABASE="${PGDATABASE:-labdb}"
while true; do
  date
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -c "SELECT * FROM clients_lab ORDER BY id;"
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -c "SELECT * FROM operations_lab ORDER BY id;"
  sleep 2
done
