#!/usr/bin/env bash
set -euo pipefail

PGDATA_PATH="${1:-/var/lib/postgresql/14/main}"
TARGET_AVAIL_MB="${2:-32}"
TRASH_DIR="$PGDATA_PATH/trash"
DB_NAME="${DB_NAME:-labdb}"
STOP_POSTGRES_AFTER_FAIL="${STOP_POSTGRES_AFTER_FAIL:-1}"

if [ ! -d "$PGDATA_PATH" ]; then
  echo "PGDATA path does not exist: $PGDATA_PATH" >&2
  exit 1
fi

mkdir -p "$TRASH_DIR"
chown postgres:postgres "$TRASH_DIR" || true

echo "Заполняем раздел с PGDATA: $PGDATA_PATH, пока свободно не станет <= ${TARGET_AVAIL_MB} МБ"
df -h "$PGDATA_PATH"

i=1
while true; do
  AVAIL_KB=$(df -P "$PGDATA_PATH" | awk 'NR==2 {print $4}')
  AVAIL_MB=$((AVAIL_KB / 1024))
  USED_PERCENT=$(df -P "$PGDATA_PATH" | awk 'NR==2 {print $5}')
  echo "Свободно: ${AVAIL_MB} МБ, занято: ${USED_PERCENT}"

  if [ "$AVAIL_MB" -le "$TARGET_AVAIL_MB" ]; then
    echo "Целевое заполнение достигнуто."
    break
  fi

  if [ "$AVAIL_MB" -gt 2048 ]; then
    CHUNK_MB=512
  elif [ "$AVAIL_MB" -gt 512 ]; then
    CHUNK_MB=128
  elif [ "$AVAIL_MB" -gt 128 ]; then
    CHUNK_MB=32
  else
    CHUNK_MB=8
  fi

  FILE="$TRASH_DIR/trash_${i}.bin"
  echo "Создаём мусорный файл $FILE (${CHUNK_MB} МБ)..."
  if command -v fallocate >/dev/null 2>&1; then
    sudo -u postgres fallocate -l "${CHUNK_MB}M" "$FILE" || true
  else
    sudo -u postgres dd if=/dev/zero of="$FILE" bs=1M count="$CHUNK_MB" status=none || true
  fi
  sync || true
  i=$((i+1))
done

echo "Добиваем оставшееся свободное место..."
sudo -u postgres dd if=/dev/zero of="$TRASH_DIR/final.bin" bs=1M status=none || true
sync || true

df -h "$PGDATA_PATH"

echo "Провоцируем ошибку записи PostgreSQL..."
set +e
sudo -u postgres psql -d "$DB_NAME" -v ON_ERROR_STOP=1 -c "DROP TABLE IF EXISTS disk_fill_test; CREATE TABLE disk_fill_test AS SELECT generate_series(1, 10000000) AS id, repeat('x', 1000) AS payload;"
WRITE_RC=$?
set -e

if [ "$WRITE_RC" -eq 0 ]; then
  echo "Тяжёлая запись неожиданно прошла. Создаём ещё одну запись для генерации WAL..."
  set +e
  sudo -u postgres psql -d "$DB_NAME" -v ON_ERROR_STOP=1 -c "INSERT INTO disk_fill_test SELECT generate_series(1, 10000000), repeat('y', 1000);"
  WRITE_RC=$?
  set -e
fi

if [ "$WRITE_RC" -ne 0 ]; then
  echo "PostgreSQL ожидаемо получил ошибку записи после заполнения диска."
else
  echo "PostgreSQL не упал на записи, но диск заполнен."
fi

if [ "$STOP_POSTGRES_AFTER_FAIL" = "1" ]; then
  echo "Останавливаем PostgreSQL на основном узле, чтобы завершить симуляцию отказа и дать скрипту доступности обнаружить сбой."
  systemctl stop postgresql || true
fi

echo "Релевантные строки лога PostgreSQL:"
grep -i "no space\|could not\|panic\|fatal\|error" /var/log/postgresql/postgresql-14-main.log 2>/dev/null | tail -n 30 || true

echo "Симуляция переполнения завершена. Папка с мусорными файлами: $TRASH_DIR"
