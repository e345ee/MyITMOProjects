# Demo Commands

## Role Check

On primary:

```bash
sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"
```

Expected: `f`.

On standby:

```bash
sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"
```

Expected: `t`.

## Replication Check

On primary:

```bash
sudo -u postgres psql -d labdb -c "SELECT client_addr, state, sync_state FROM pg_stat_replication;"
```

## PgBouncer Connection

On client:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb
```

## Test Transaction

```sql
BEGIN;
INSERT INTO clients_lab(name, balance) VALUES ('DemoClient', 1000);
INSERT INTO operations_lab(client_id, amount, operation_type)
VALUES (currval('clients_lab_id_seq'), 1000, 'demo_transaction');
COMMIT;
```

## Disk Filling

On pg1:

```bash
bash /usr/local/bin/fill_pgdata_disk.sh /var/lib/postgresql/14/main 1
```

## PostgreSQL Logs

```bash
sudo journalctl -u postgresql -n 100 | grep -Ei "no space|could not|error|fatal|panic"
sudo find /var/lib/postgresql/14/main/log -type f -maxdepth 1 -print -exec tail -n 50 {} \;
```

## Auto-Failover Logs

On client:

```bash
sudo tail -f /var/log/pg_auto_failover.log
```

## Timer Status

```bash
systemctl list-timers | grep pg-auto
sudo systemctl status pg-auto-failover.timer
```

## Stopping the Timer Before Recovery

```bash
sudo systemctl stop pg-auto-failover.timer
```


The `fill_pgdata_disk.sh` script fills the PGDATA partition, triggers a heavy test write in PostgreSQL, prints relevant errors from the logs, and stops PostgreSQL on `pg1` so auto-failover reliably detects the failure. To disable the stop step, run it as: `STOP_POSTGRES_AFTER_FAIL=0 bash /usr/local/bin/fill_pgdata_disk.sh`.
