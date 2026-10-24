# Distributed Lovable

A microservices take on [Spring Lovable Clone](https://github.com/xaviermaldonadony/spring-lovable-clone) — an AI-powered app builder inspired by Lovable.

This repository is the **microservice version of the original monolith**. The goal is to take the working single-application codebase and split it into independently deployable services with centralized configuration, service discovery, and a single API entry point.

> **Status:** work in progress. Modules are being migrated from the monolith one service at a time, so not every feature is wired up yet.

## Relationship to the monolith

| Repository | Description |
| --- | --- |
| [spring-lovable-clone](https://github.com/xaviermaldonadony/spring-lovable-clone) | The original Spring Boot monolith. Contains the full feature set and is the source of truth for behavior. |
| **distributed-lovable** (this repo) | Microservice refactor of the same application. Same domain, split across services. |

## Architecture

```
                        ┌──────────────────┐
      clients ────────▶ │    api-gateway   │
                        └────────┬─────────┘
                                 │
        ┌────────────────────────┼──────────────────────────┐
        ▼                        ▼                          ▼
┌────────────────┐      ┌─────────────────┐       ┌────────────────────┐
│ account-service│      │ workspace-service│      │ intelligence-service│
│ auth, users,   │      │ projects,        │      │ AI chat and code    │
│ plans, billing │      │ collaboration    │      │ generation          │
└───────┬────────┘      └────────┬─────────┘       └─────────┬──────────┘
        └───────────────┬────────┴───────────────────────────┘
                        ▼
              ┌────────────────────┐
              │     common-lib     │  shared DTOs, enums, errors,
              │   (shared JAR)     │  JWT security, Feign clients
              └────────────────────┘

  config-service    → centralized configuration
  discovery-service → service registry (Eureka)
```

## Services

| Module | Responsibility |
| --- | --- |
| `api-gateway` | Single entry point for clients. Routes requests to the backing services (Spring Cloud Gateway, reactive stack). |
| `config-service` | Centralized configuration for all services (Spring Cloud Config). |
| `discovery-service` | Service registry and lookup (Netflix Eureka). |
| `account-service` | Signup/login, JWT authentication, user profiles, plans, usage limits, and Stripe subscriptions. |
| `workspace-service` | Projects/workspaces, membership, roles, and permissions. |
| `intelligence-service` | AI features: chat, code generation, and OpenAI integration via Spring AI. |
| `common-lib` | Shared library: DTOs, enums, error handling, JWT security filter, and shared configuration. |

## Tech stack

- **Java 21**
- **Spring Boot 4.1.x**
- **Spring Cloud 2025.1.x** — Gateway, Config, Eureka, OpenFeign
- **Spring AI** — OpenAI model integration
- **Spring Security + JWT** (`jjwt`)
- **Spring Data JPA + PostgreSQL**
- **Stripe** for payments/subscriptions
- **Lombok**, **MapStruct**
- **Maven** (each module ships with the Maven wrapper)

## Repository layout

```
distributed-lovable/
├── account-service/
├── api-gateway/
├── common-lib/
├── config-service/
├── discovery-service/
├── intelligence-service/
└── workspace-service/
```

Each module is a standalone Maven project. There is intentionally **no parent/aggregator `pom.xml`** yet, so modules are built individually.

## Prerequisites

- JDK 21
- Docker (for PostgreSQL and other backing services as they are added)
- No global Maven install required — use the included `./mvnw`

## Getting started

### 1. Build and install the shared library

Every service depends on `common-lib`, so install it into your local Maven repository first:

```bash
cd common-lib
./mvnw install
```

### 2. Start the infrastructure services

In separate terminals:

```bash
cd config-service && ./mvnw spring-boot:run
cd discovery-service && ./mvnw spring-boot:run
```

### 3. Start the application services

Once config and discovery are up, start the domain services and the gateway:

```bash
cd account-service && ./mvnw spring-boot:run
cd workspace-service && ./mvnw spring-boot:run
cd intelligence-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

Requests should go through the gateway rather than calling individual services directly.

## Configuration

Each service reads `src/main/resources/application.yaml` for its name and settings. Keep real credentials out of Git and pass them as environment variables (or local, git-ignored config files):

| Property | Environment variable | Used by |
| --- | --- | --- |
| `jwt.secret-key` | `JWT_SECRET_KEY` | `common-lib`, all services |
| `stripe.api.secret-key` | `STRIPE_API_SECRET_KEY` | `account-service` |
| `spring.ai.openai.api-key` | `SPRING_AI_OPENAI_API_KEY` | `intelligence-service` |

The root `.gitignore` excludes IDE files, build output, logs, `.env` files, and local configuration overrides.

## Migration notes

- Feature behavior is ported from the monolith, not reinvented.
- Shared code that was previously internal to the monolith now lives in `common-lib`.
- Cross-service calls are expected to go through service discovery and the gateway as the migration progresses.
