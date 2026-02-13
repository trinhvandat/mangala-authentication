#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

CONTEXT_FILE="docs/context-map.yaml"
ADR_DIR="docs/adr"

if [[ ! -f "$CONTEXT_FILE" ]]; then
  echo "[FAIL] Missing $CONTEXT_FILE"
  exit 1
fi

if [[ ! -d "$ADR_DIR" ]]; then
  echo "[FAIL] Missing $ADR_DIR"
  exit 1
fi

# Collect changed files including untracked files
CHANGED_FILES="$(git status --porcelain | awk '{print $2}')"

if [[ -z "$CHANGED_FILES" ]]; then
  echo "[OK] No file changes detected."
  exit 0
fi

needs_doc_update=0

if echo "$CHANGED_FILES" | grep -q '^src/main/resources/migration/'; then
  needs_doc_update=1
fi

if echo "$CHANGED_FILES" | grep -qE '^src/main/java/.*/(adapter/web|usecase|domain|adapter/repository)/'; then
  if echo "$CHANGED_FILES" | grep -qE 'internal|policy|permission|role'; then
    needs_doc_update=1
  fi
fi

if [[ "$needs_doc_update" -eq 1 ]]; then
  if ! echo "$CHANGED_FILES" | grep -q '^docs/context-map.yaml'; then
    echo "[WARN] Policy/schema-related code changed but docs/context-map.yaml was not updated."
    exit 2
  fi
fi

echo "[OK] Context sync check passed."
