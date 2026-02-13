# ADR 0001: Policy Loading Via Auth Internal API

## Status
Accepted

## Context
`mangala-gateway` enforces authorization but policy data is owned by `mangala-authentication`.
Direct DB access from gateway into auth schema increases coupling and breaks service boundaries.

## Decision
Gateway must load policies from auth internal HTTP APIs:
- `GET /v1/internal/policies`
- `GET /v1/internal/policies/version`

`/v1/internal/policies/version` is used as a lightweight change detector.
When remote version is newer, gateway reloads full snapshot from `/v1/internal/policies` and atomically swaps in-memory cache.

## Consequences
- Pros:
  - Preserves auth service ownership of policy model.
  - Reduces coupling to auth DB schema.
  - Simplifies gateway cache consistency model.
- Cons:
  - Requires internal API hardening (service-to-service auth, ideally mTLS).
  - Requires operational reliability for auth internal endpoints.
