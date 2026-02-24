# AGENTS.md

## Purpose
This file is the fast bootstrap context for contributors and coding agents.
Read this first before scanning the whole codebase.

## Service Scope
- Service: `mangala-authentication`
- Base package: `org.mangala.authentication`
- Main class: `src/main/java/org/mangala/authentication/MainApplication.java`

## Architecture Conventions
- Feature-first packages: `auth`, `passkey`, `user`, `policy`, `shared`.
- Layering per feature:
  - `adapter/web`: controllers, request/response DTOs, web mappers
  - `usecase`: application services (`XxxUseCase`, `XxxUseCaseImpl`)
  - `usecase/command`: input models (records/builders)
  - `usecase/model`: output models (records)
  - `domain`: entities + domain exceptions
  - `adapter/repository`: Spring Data repositories

## Naming Conventions
- Java fields/methods/variables: `camelCase`.
- DB column names remain snake_case via `@Column(name = "...")`.
- UseCase pair naming:
  - Interface: `XxxUseCase`
  - Implementation: `XxxUseCaseImpl`
- Web DTOs:
  - Request: `...RequestDTO`
  - Response: `...ResponseDTO`

## Error Handling
- Domain/business exceptions extend `org.mangala.exception.BaseException`.
- Error definitions must be in `shared/exception/ErrorConstant`.

## Persistence and Migrations
- Flyway location: `src/main/resources/migration`.
- Naming: `V{N}__{description}.sql`.
- Current authorization-related source tables:
  - `permissions`, `roles`, `role_permissions`, `user_roles`, `api_permissions`, `policy_version`

## Internal Policy APIs (Contract for Gateway)
- `GET /v1/internal/policies`
- `GET /v1/internal/policies/version`
- Security is configurable mTLS/x509 via `application.security.internal-api.*`.

## Documentation Update Rule
Update these docs when policy schema or internal API contract changes:
- `docs/context-map.yaml`
- `docs/adr/*` (new decision or changed decision)

Use `scripts/check-context-sync.sh` for a lightweight consistency check.
