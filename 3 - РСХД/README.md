# Распределенные системы хранения данных

![Распределенные системы хранения данных](media/rshd.jpg)

Материалы по дисциплине «Распределенные системы хранения данных» за 3 курс.

Первые две лабораторные опубликованы в репозиториях коллеги, поэтому здесь
оставлены ссылки на них. Третья лабораторная добавлена локально в папку
[`lab3`](lab3/): из архива удалены служебные файлы, старые PDF и лишние
материалы, исходники очищены от комментариев, отчет пересобран из Typst.

## Стек

![PostgreSQL](https://img.shields.io/badge/PostgreSQL_14%2F16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![PgBouncer](https://img.shields.io/badge/PgBouncer-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Ansible](https://img.shields.io/badge/Ansible-EE0000?style=for-the-badge&logo=ansible&logoColor=white)
![SQL](https://img.shields.io/badge/SQL-025E8C?style=for-the-badge)
![Bash](https://img.shields.io/badge/Bash-4EAA25?style=for-the-badge&logo=gnubash&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![rsync](https://img.shields.io/badge/rsync-0F766E?style=for-the-badge)
![WAL/PITR](https://img.shields.io/badge/WAL%20%2F%20PITR-7C3AED?style=for-the-badge)
![FreeBSD](https://img.shields.io/badge/FreeBSD_14-AB2B28?style=for-the-badge&logo=freebsd&logoColor=white)
![Ubuntu](https://img.shields.io/badge/Ubuntu_22.04-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)
![systemd](https://img.shields.io/badge/systemd-111827?style=for-the-badge&logo=linux&logoColor=white)
![Typst](https://img.shields.io/badge/Typst-239DAD?style=for-the-badge&logo=typst&logoColor=white)

## Ссылки и состав

- [ЛР1: конфигурация PostgreSQL](https://github.com/Vaneshik/rshd-lab1)

  Развертывание и настройка кластера PostgreSQL под OLAP-нагрузку:
  инициализация `PGDATA`, настройка `pg_hba.conf`, параметров сервера через
  `ALTER SYSTEM`, логирования, табличных пространств, ролей, базы данных и
  тестового наполнения.

- [ЛР2: резервное копирование и восстановление](https://github.com/Vaneshik/rshd-lab2)

  Сценарии disaster recovery для PostgreSQL 16 на FreeBSD: холодный полный
  `rsync`-бэкап по cron с ротацией 14 копий, запуск СУБД на резервном узле,
  восстановление после повреждения конфигурации и PITR-восстановление через
  архивные WAL до момента перед `DROP TABLE`.

- [ЛР3: PostgreSQL HA + PgBouncer](lab3/)

  Локально добавленный стенд на Ansible для трех Ubuntu-машин: `pg1` как
  primary, `pg2` как standby, `client` как узел с PgBouncer, Ansible и
  проверкой доступности. В лабораторной настраивается потоковая репликация,
  read/write и read-only входы PgBouncer, симулируется отказ заполнением
  раздела с `PGDATA`, выполняется автоматический failover на standby и ручное
  восстановление исходной схемы.

