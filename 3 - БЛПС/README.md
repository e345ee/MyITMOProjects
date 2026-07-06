# Бизнес-логика программных систем

![Бизнес-логика программных систем](media/blps.jpg)

Материалы по дисциплине «Бизнес-логика программных систем» за 3 курс.

В репозитории представлены только лабораторные работы №3 и №4: третья
лабораторная развивала решения из ЛР1/ЛР2, а четвёртая перерабатывала
предыдущий бизнес-процесс под Camunda.

Предметная область обеих работ - процесс обработки откликов на вакансии
в системе, похожей на hh.ru: кандидат подаёт отклик, система выполняет
автоматический скрининг, рекрутер принимает решение, кандидат получает
уведомления и отвечает на приглашение.

## Ссылки

- [ЛР3: HH Process + Kafka](https://github.com/e345ee/hh-process-kafka)

  Доработка приложения из ЛР2: асинхронная обработка откликов через
  Apache Kafka и ZooKeeper, разделение ролей `app-api` и `app-worker`,
  плановые задачи через `@Scheduled`, WebSocket-уведомления и интеграция
  с внешней EIS/Odoo через JCA.

  ![Java](https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
  ![Spring Kafka](https://img.shields.io/badge/Spring_Kafka-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
  ![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
  ![ZooKeeper](https://img.shields.io/badge/ZooKeeper-5780A8?style=for-the-badge&logo=apache&logoColor=white)
  ![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
  ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
  ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
  ![Narayana](https://img.shields.io/badge/Narayana_JTA-6A5ACD?style=for-the-badge)
  ![WebSocket](https://img.shields.io/badge/WebSocket_STOMP-010101?style=for-the-badge&logo=socketdotio&logoColor=white)
  ![JCA](https://img.shields.io/badge/Jakarta_Connectors_JCA-8B5CF6?style=for-the-badge)
  ![Odoo](https://img.shields.io/badge/Odoo_EIS-714B67?style=for-the-badge&logo=odoo&logoColor=white)
  ![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
  ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

- [ЛР4: HH Process + Camunda](https://github.com/e345ee/hh-process-camunda)

  Переработка процесса на BPMS: бизнес-логика вынесена в BPMN 2.0 и DMN,
  Camunda работает как standalone-сервис, пользовательские действия
  доступны через Camunda Tasklist и Forms, а Spring Boot-приложение
  выполняет бизнес-операции через external tasks и разворачивается на
  WildFly.

  ![Java](https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
  ![Camunda](https://img.shields.io/badge/Camunda_7.23-FC5D0D?style=for-the-badge&logo=camunda&logoColor=white)
  ![BPMN](https://img.shields.io/badge/BPMN_2.0-0F172A?style=for-the-badge)
  ![DMN](https://img.shields.io/badge/DMN-334155?style=for-the-badge)
  ![Camunda Forms](https://img.shields.io/badge/Camunda_Forms-FC5D0D?style=for-the-badge&logo=camunda&logoColor=white)
  ![External Tasks](https://img.shields.io/badge/External_Tasks-7C2D12?style=for-the-badge)
  ![WildFly](https://img.shields.io/badge/WildFly-50A4D8?style=for-the-badge)
  ![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
  ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
  ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
  ![Narayana](https://img.shields.io/badge/Narayana_JTA-6A5ACD?style=for-the-badge)
  ![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
  ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

## Кратко по работам

- ЛР3 - реализация асинхронного скрининга заявок по модели подписки: `application.submitted` -> worker -> `application.screened`, распределение обработки между узлами, закрытие просроченных приглашений и экспорт интервью во внешнюю EIS.
- ЛР4 - перенос управляющей логики в Camunda: процессы вакансии, отклика, отмены интервью, изменения статуса вакансии, обработки таймаутов и уведомлений; автоскрининг и шаблоны уведомлений вынесены в DMN.
