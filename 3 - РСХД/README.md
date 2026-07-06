# Distributed Data Storage Systems

![Distributed Data Storage Systems](media/rshd.jpg)

Materials for the third-year Distributed Data Storage Systems course.

The first two labs were published in a teammate's repositories, so this section
keeps links to them. The third lab is added locally in [`lab3`](lab3/): service
files, old PDFs, and unnecessary materials were removed from the archive, source
files were cleaned of comments, and the report was rebuilt from Typst.

## Stack

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

## Links and Contents

- [Lab 1: PostgreSQL configuration](https://github.com/Vaneshik/rshd-lab1)

  Deployment and configuration of a PostgreSQL cluster for OLAP workloads:
  `PGDATA` initialization, `pg_hba.conf` setup, server parameters through
  `ALTER SYSTEM`, logging, tablespaces, roles, database, and test data setup.

- [Lab 2: Backup and recovery](https://github.com/Vaneshik/rshd-lab2)

  Disaster recovery scenarios for PostgreSQL 16 on FreeBSD: cold full `rsync`
  backup via cron with 14-copy rotation, starting the DBMS on a backup node,
  recovery after configuration damage, and PITR recovery through archived WAL
  up to the moment before `DROP TABLE`.

- [Lab 3: PostgreSQL HA + PgBouncer](lab3/)

  Locally added Ansible stand for three Ubuntu machines: `pg1` as primary,
  `pg2` as standby, and `client` as the node with PgBouncer, Ansible, and
  availability checks. The lab configures streaming replication, read/write and
  read-only PgBouncer entries, simulates failure by filling the `PGDATA`
  partition, performs automatic failover to standby, and manually restores the
  original topology.
