#!/usr/bin/env bash
set -euo pipefail
PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-6432}"
PGUSER="${PGUSER:-appuser}"
PGDATABASE="${PGDATABASE:-labdb}"
while true; do
  TS=$(date +%s)
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" <<SQL
BEGIN;
INSERT INTO clients_lab(name, balance) VALUES ('AutoClient_$TS', 1000);
INSERT INTO operations_lab(client_id, amount, operation_type)
VALUES (currval('clients_lab_id_seq'), 1000, 'auto_write_before_or_after_failover');
COMMIT;
SQL
  sleep 3
done
