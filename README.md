# mangala-authentication
authentication service for mangala wallet

## Internal Policy API

This service exposes internal APIs used by `mangala-gateway` to load authorization policies:

- `GET /v1/internal/policies`
- `GET /v1/internal/policies/version`

Details and security model are documented at:

- `docs/internal-policy-api.md`

## Context Pack

Use these files to bootstrap quickly in new sessions:

- `AGENTS.md`
- `docs/context-map.yaml`
- `docs/adr/`
- `scripts/check-context-sync.sh`

Run consistency check:

```bash
./scripts/check-context-sync.sh
```
