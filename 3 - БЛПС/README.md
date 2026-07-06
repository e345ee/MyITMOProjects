# Business Logic of Software Systems

![Business Logic of Software Systems](media/blps.jpg)

Materials for the third-year Business Logic of Software Systems course.

The repository includes only lab works 3 and 4: the third lab extended the
solutions from lab 1/lab 2, while the fourth reworked the previous business
process for Camunda.

The domain of both works is processing job applications in a system similar to
hh.ru: a candidate submits an application, the system performs automatic
screening, a recruiter makes a decision, and the candidate receives
notifications and responds to invitations.

## Links

- [Lab 3: HH Process + Kafka](https://github.com/e345ee/hh-process-kafka)

  Improvement of the application from lab 2: asynchronous application
  processing through Apache Kafka and ZooKeeper, separation of `app-api` and
  `app-worker` roles, scheduled tasks through `@Scheduled`, WebSocket
  notifications, and integration with an external EIS/Odoo through JCA.

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

- [Lab 4: HH Process + Camunda](https://github.com/e345ee/hh-process-camunda)

  BPMS-based process redesign: business logic is moved to BPMN 2.0 and DMN,
  Camunda runs as a standalone service, user actions are available through
  Camunda Tasklist and Forms, and the Spring Boot application performs business
  operations through external tasks and is deployed on WildFly.

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

## Work Summary

- Lab 3 - implementation of asynchronous application screening with a subscription model: `application.submitted` -> worker -> `application.screened`, distributed processing between nodes, closing expired invitations, and exporting interviews to an external EIS.
- Lab 4 - moving control logic to Camunda: vacancy, application, interview cancellation, vacancy status change, timeout handling, and notification processes; auto-screening and notification templates are moved to DMN.
