#!/usr/bin/env bash
set -euo pipefail
cat <<'RUNBOOK'
Ручное восстановление после автоматического failover
===================================================

После второго этапа состояние такое:
  pg2 — новый primary
  pg1 — старый primary, на нём был заполнен диск
  PgBouncer на client смотрит на pg2

1. На client останавливаем автоматический failover, чтобы он не мешал восстановлению:

  sudo systemctl stop pg-auto-failover.timer

2. На pg1 удаляем мусорные файлы, которыми заполняли PGDATA:

  sudo rm -rf /var/lib/postgresql/14/main/trash
  sudo rm -f /var/lib/postgresql/14/main/trashfile*
  df -h /var/lib/postgresql/14/main

3. На pg1 останавливаем PostgreSQL и пересоздаём его как standby от pg2.
   Вместо PG2_PRIVATE_IP указываем IP сервера pg2.
   Вместо REPL_PASS указываем пароль пользователя replicator из group_vars/all.yml.

  sudo systemctl stop postgresql
  sudo rm -rf /var/lib/postgresql/14/main/*
  sudo -u postgres bash
  export PGPASSWORD='REPL_PASS'
  pg_basebackup -h PG2_PRIVATE_IP -D /var/lib/postgresql/14/main -U replicator -P -R -X stream
  exit
  sudo systemctl start postgresql
  sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"

4. На client временно останавливаем PgBouncer, чтобы во время обратного переключения не было записей:

  sudo systemctl stop pgbouncer

5. На pg1 повышаем сервер обратно до primary:

  sudo -u postgres /usr/lib/postgresql/14/bin/pg_ctl promote -D /var/lib/postgresql/14/main
  sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"

6. На pg2 пересоздаём standby от pg1.
   Вместо PG1_PRIVATE_IP указываем IP сервера pg1.
   Вместо REPL_PASS указываем пароль пользователя replicator.

  sudo systemctl stop postgresql
  sudo rm -rf /var/lib/postgresql/14/main/*
  sudo -u postgres bash
  export PGPASSWORD='REPL_PASS'
  pg_basebackup -h PG1_PRIVATE_IP -D /var/lib/postgresql/14/main -U replicator -P -R -X stream
  exit
  sudo systemctl start postgresql
  sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"

7. На client вручную возвращаем PgBouncer на pg1.
   Открываем конфиг:

  sudo nano /etc/pgbouncer/pgbouncer.ini

   В секции [databases] должны быть такие строки:

  labdb = host=PG1_PRIVATE_IP port=5432 dbname=labdb
  labdb_rw = host=PG1_PRIVATE_IP port=5432 dbname=labdb
  labdb_ro = host=PG2_PRIVATE_IP port=5432 dbname=labdb

   labdb и labdb_rw ведут на primary pg1.
   labdb_ro ведёт на standby pg2 для чтения с реплики.
   Сохраняем файл и перезапускаем PgBouncer:

  echo 'PG1_PRIVATE_IP' | sudo tee /var/lib/pgbouncer/current_primary
  sudo systemctl start pgbouncer
  sudo systemctl start pg-auto-failover.timer

8. На client проверяем работу через PgBouncer.
   Для чтения/записи подключаемся к labdb_rw:

  psql -h 127.0.0.1 -p 6432 -U appuser -d labdb_rw

  SELECT * FROM clients_lab;
  SELECT * FROM operations_lab;
  BEGIN;
  INSERT INTO clients_lab(name, balance) VALUES ('AfterManualRecovery', 3000);
  INSERT INTO operations_lab(client_id, amount, operation_type)
  VALUES (currval('clients_lab_id_seq'), 3000, 'after_manual_recovery');
  COMMIT;

9. На client проверяем чтение с реплики через PgBouncer:

  psql -h 127.0.0.1 -p 6432 -U appuser -d labdb_ro

  SELECT pg_is_in_recovery();
  SELECT * FROM clients_lab;

   Если попробовать INSERT в labdb_ro, будет ошибка, потому что standby доступен только на чтение.

10. На pg2 проверяем, что он снова standby и видит новые данные:

  sudo -u postgres psql -d labdb -c "SELECT pg_is_in_recovery();"
  sudo -u postgres psql -d labdb -c "SELECT * FROM clients_lab;"

RUNBOOK
