# Lab Work 3: PostgreSQL + PgBouncer

Stand:

| Node | IP | Role |
|---|---|---|
| `pg1` | `203.0.113.10` | PostgreSQL primary before failure |
| `pg2` | `203.0.113.11` | PostgreSQL standby before failure |
| `client` | `203.0.113.12` | PgBouncer, Ansible, psql, auto-failover |

Ansible runs from `client`.

## Quick Start

```bash
ssh root@203.0.113.12
cd /root/lab3-postgres-ha
ansible all -i inventory.ini -m ping
ansible-playbook -i inventory.ini site.yml
```

Connection check through PgBouncer:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb
```

PgBouncer has three entry points:

```text
labdb    -> current primary
labdb_rw -> primary for read/write access
labdb_ro -> standby for replica reads
```

Replica read check:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb_ro
```

Writes to the replica are not allowed. This is expected: standby works in read-only mode.

## What to Change

The main variables are located in:

```bash
group_vars/all.yml
```

Usually changed values:

```yaml
app_password: "<APP_PASSWORD>"
replication_password: "<REPLICATION_PASSWORD>"
pg1_private_ip: "10.0.0.10"
pg2_private_ip: "10.0.0.11"
client_private_ip: "10.0.0.12"
```

If there is no private network, public IPs are also used as working addresses.

## Stage 2

Client connections are started on `client`:

```bash
./scripts/open_read_client.sh
./scripts/open_write_client.sh
```

Disk filling is started on `pg1`:

```bash
ssh root@203.0.113.10
bash /usr/local/bin/fill_pgdata_disk.sh
```

Watch the automatic failover log on `client`:

```bash
sudo tail -f /var/log/pg_auto_failover.log
```

## Stage 3

The manual recovery scenario is located at:

```bash
./scripts/manual_recovery_switchback.sh
```

It prints commands and explanations:

```bash
./scripts/manual_recovery_switchback.sh
```

## Stand Cleanup

For a full reset before rerunning:

```bash
ansible-playbook -i inventory.ini cleanup.yml
ansible-playbook -i inventory.ini site.yml
```

## Passwordless SSH Access

On the first run, the playbook can be launched with `-k -K` so Ansible connects using the root password. After that, Ansible creates an SSH key on the `client` machine and adds it to `pg1` and `pg2`.

First run:

```bash
ansible-playbook -i inventory.ini site.yml -k -K
```

Subsequent runs can be performed without entering a password:

```bash
ansible-playbook -i inventory.ini site.yml
```

The failover script also uses this key so the `client` machine can run `pg_ctl promote` on `pg2` over SSH without a password.


The `fill_pgdata_disk.sh` script fills the PGDATA partition, triggers a heavy test write in PostgreSQL, prints relevant errors from the logs, and stops PostgreSQL on `pg1` so auto-failover reliably detects the failure. To disable the stop step, run it as: `STOP_POSTGRES_AFTER_FAIL=0 bash /usr/local/bin/fill_pgdata_disk.sh`.
