# JM-JobPostService

Job posting management service for the Job Manager platform.

## Overview

The Job Post Service manages job listings created by companies, including job details, skill requirements, employment types, salary information, and publication status. It enforces subscription-based limits on job posting.

## Features

- **Job CRUD Operations**: Create, read, update, delete job posts
- **Skill Tag Management**: Link jobs to required skill tags
- **Employment Type Filtering**: Full-time, Part-time, Internship, Contract, Fresher
- **Salary Management**: Range, Estimation, or Negotiable
- **Publication Control**: Draft and publish workflows
- **Expiry Management**: Automatic job post expiration
- **Search & Filter**: Advanced job search capabilities
- **Subscription Enforcement**: Freemium (3 jobs max) vs Premium (unlimited)

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**: Database persistence
- **PostgreSQL**: Job posts database
- **Kafka**: Event-driven communication
- **BitSet**: Efficient employment type storage
- **Lombok**: Reduce boilerplate code

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL database
- Kafka broker
- JM-SkillTagService running
- JM-CompanySubscriptionService running

## Database Schema

### Table: `job_post`

| Column            | Type       | Description                          |
| ----------------- | ---------- | ------------------------------------ |
| `postid`          | UUID (PK)  | Job post identifier (auto-generated) |
| `companyid`       | UUID       | Company ID (FK to Auth service)      |
| `title`           | VARCHAR    | Job title                            |
| `description`     | TEXT(5000) | Job description                      |
| `city`            | VARCHAR    | Job location (city)                  |
| `country`         | VARCHAR    | Job location (country)               |
| `employment_type` | BITSET     | Employment types (bit flags)         |
| `salary_title`    | ENUM       | RANGE, ESTIMATION, NEGOTIABLE        |
| `salary_min`      | DECIMAL    | Minimum salary                       |
| `salary_max`      | DECIMAL    | Maximum salary                       |
| `posted_date`     | TIMESTAMP  | Auto-generated on creation           |
| `expiry_date`     | TIMESTAMP  | When job post expires                |
| `is_published`    | BOOLEAN    | Publication status                   |

### Employment Type BitSet

```
Bit 0: FULL_TIME
Bit 1: PART_TIME
Bit 2: FRESHER
Bit 3: INTERNSHIP
Bit 4: CONTRACT
```

## Data Seeding

The service automatically seeds 10 job posts on startup:

### Freemium Companies (2 posts each)

**NAB - Financial Services**

1. FinTech Software Developer (Java, Spring Boot, REST API)
2. Cloud Infrastructure Engineer (AWS, Docker, Kubernetes)

**Google Vietnam - Technology**

1. Frontend Engineer (React, TypeScript, HTML, CSS)
2. Machine Learning Engineer (Python, AWS, Docker)

### Premium Companies (3 posts each)

**Netcompany - Software Engineering Domain**

1. Full-stack Software Engineer Intern (React, Spring Boot, Docker) - INTERNSHIP
2. Senior Java Backend Developer (Java, Spring Boot, Docker, Kubernetes) - FULL_TIME
3. React Frontend Developer (React, TypeScript, HTML, CSS) - FULL_TIME

**Shopee Singapore - Data Engineering Domain**

1. Senior Data Engineer (Contract) (Python, AWS, Snowflake) - CONTRACT
2. Data Platform Engineer (Python, AWS, Snowflake, Docker) - FULL_TIME
3. Analytics Engineer (Python, Snowflake) - FULL_TIME

> **Note**: JobPost entity uses `@GeneratedValue` for auto-generated UUIDs, unlike other services.

## Skill Tag IDs (from SkillTagService)

```
1:  JAVA              11: MONGODB
2:  PYTHON            12: DOCKER
3:  JAVASCRIPT        13: KUBERNETES
4:  TYPESCRIPT        14: AWS
5:  REACT             15: AZURE
6:  ANGULAR           16: COMMUNICATION
7:  VUE               17: REST API
8:  SPRING BOOT       18: GRAPHQL
9:  NODE.JS           19: HTML
10: SNOWFLAKE         20: CSS
```

## Job Post Limits by Subscription

| Subscription Tier | Max Active Jobs |
| ----------------- | --------------- |
| Freemium          | 3               |
| Premium           | Unlimited       |
