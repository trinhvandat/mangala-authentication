# ADR 0002: Protect Internal Policy APIs With Optional mTLS

## Status
Accepted

## Context
`/v1/internal/policies` and `/v1/internal/policies/version` are consumed by gateway and contain authorization policy data.
Leaving these endpoints open increases risk of policy exfiltration and unauthorized access.

## Decision
Add dedicated security configuration for `/v1/internal/**`:
- Support x509 client certificate authentication.
- Extract client principal from certificate DN using configurable regex.
- Authorize only configured principal values and grant `INTERNAL_POLICY_READ` authority.
- Keep `mtls-enabled=false` default for local development compatibility.

Config keys:
- `application.security.internal-api.mtls-enabled`
- `application.security.internal-api.subject-principal-regex`
- `application.security.internal-api.allowed-principals`

## Consequences
- Pros:
  - Enforces service identity for internal policy APIs in production.
  - Reduces risk of unauthorized internal policy reads.
- Cons:
  - Requires certificate issuance/rotation and environment TLS setup.
  - Misconfigured CN allowlist can block gateway bootstrap.
