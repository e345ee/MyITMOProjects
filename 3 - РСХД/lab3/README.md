# Лабораторная работа №3: PostgreSQL + PgBouncer

Стенд:

| Узел | IP | Роль |
|---|---|---|
| `pg1` | `203.0.113.10` | PostgreSQL primary до сбоя |
| `pg2` | `203.0.113.11` | PostgreSQL standby до сбоя |
| `client` | `203.0.113.12` | PgBouncer, Ansible, psql, auto-failover |

Ansible запускается с `client`.

## Быстрый запуск

```bash
ssh root@203.0.113.12
cd /root/lab3-postgres-ha
ansible all -i inventory.ini -m ping
ansible-playbook -i inventory.ini site.yml
```

Проверка подключения через PgBouncer:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb
```

В PgBouncer есть три входа:

```text
labdb    -> текущий primary
labdb_rw -> primary для чтения/записи
labdb_ro -> standby для чтения с реплики
```

Проверка чтения с реплики:

```bash
psql -h 127.0.0.1 -p 6432 -U appuser -d labdb_ro
```

На реплику писать нельзя. Это нормально: standby работает только на чтение.

## Что менять

Основные переменные находятся в:

```bash
group_vars/all.yml
```

Обычно меняются:

```yaml
app_password: "<APP_PASSWORD>"
replication_password: "<REPLICATION_PASSWORD>"
pg1_private_ip: "10.0.0.10"
pg2_private_ip: "10.0.0.11"
client_private_ip: "10.0.0.12"
```

Если приватной сети нет, публичные IP используются и как рабочие адреса.

## Этап 2

Клиентские подключения запускаются на `client`:

```bash
./scripts/open_read_client.sh
./scripts/open_write_client.sh
```

Переполнение диска запускается на `pg1`:

```bash
ssh root@203.0.113.10
bash /usr/local/bin/fill_pgdata_disk.sh
```

Лог автоматического failover смотреть на `client`:

```bash
sudo tail -f /var/log/pg_auto_failover.log
```

## Этап 3

Ручной сценарий восстановления находится в:

```bash
./scripts/manual_recovery_switchback.sh
```

Он выводит команды и пояснения:

```bash
./scripts/manual_recovery_switchback.sh
```

## Зачистка стенда

Для полного сброса перед повторным запуском:

```bash
ansible-playbook -i inventory.ini cleanup.yml
ansible-playbook -i inventory.ini site.yml
```

## SSH-доступ без пароля

При первом запуске playbook можно запускать с ключами `-k -K`, чтобы Ansible подключился по root-паролю. После этого Ansible сам создаёт SSH-ключ на машине `client` и добавляет его на `pg1` и `pg2`.

Первый запуск:

```bash
ansible-playbook -i inventory.ini site.yml -k -K
```

Следующие запуски можно выполнять уже без ввода пароля:

```bash
ansible-playbook -i inventory.ini site.yml
```

Failover-скрипт также использует этот ключ, чтобы машина `client` могла выполнить `pg_ctl promote` на `pg2` по SSH без пароля.


Скрипт `fill_pgdata_disk.sh` заполняет раздел с PGDATA, вызывает тестовую тяжёлую запись в PostgreSQL, печатает релевантные ошибки из логов и останавливает PostgreSQL на `pg1`, чтобы auto-failover гарантированно обнаружил отказ. Для отключения остановки можно запускать так: `STOP_POSTGRES_AFTER_FAIL=0 bash /usr/local/bin/fill_pgdata_disk.sh`.
