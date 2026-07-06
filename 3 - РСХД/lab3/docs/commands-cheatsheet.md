# Команды для демонстрации

## Проверка ролей

На primary:

```bash
sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"
```

Ожидается `f`.

На standby:

```bash
sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"
```

Ожидается `t`.

## Проверка репликации

На primary:

```bash
sudo -u postgres psql -d labdb -c "SELECT client_addr, state, sync_state FROM pg_stat_replication;"
```

## Подключение через PgBouncer

На client:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb
```

## Тестовая транзакция

```sql
BEGIN;
INSERT INTO clients_lab(name, balance) VALUES ('DemoClient', 1000);
INSERT INTO operations_lab(client_id, amount, operation_type)
VALUES (currval('clients_lab_id_seq'), 1000, 'demo_transaction');
COMMIT;
```

## Заполнение диска

На pg1:

```bash
bash /usr/local/bin/fill_pgdata_disk.sh /var/lib/postgresql/14/main 1
```

## Логи PostgreSQL

```bash
sudo journalctl -u postgresql -n 100 | grep -Ei "no space|could not|error|fatal|panic"
sudo find /var/lib/postgresql/14/main/log -type f -maxdepth 1 -print -exec tail -n 50 {} \;
```

## Логи auto-failover

На client:

```bash
sudo tail -f /var/log/pg_auto_failover.log
```

## Статус timer

```bash
systemctl list-timers | grep pg-auto
sudo systemctl status pg-auto-failover.timer
```

## Остановка timer перед восстановлением

```bash
sudo systemctl stop pg-auto-failover.timer
```


Скрипт `fill_pgdata_disk.sh` заполняет раздел с PGDATA, вызывает тестовую тяжёлую запись в PostgreSQL, печатает релевантные ошибки из логов и останавливает PostgreSQL на `pg1`, чтобы auto-failover гарантированно обнаружил отказ. Для отключения остановки можно запускать так: `STOP_POSTGRES_AFTER_FAIL=0 bash /usr/local/bin/fill_pgdata_disk.sh`.
