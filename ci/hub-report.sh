#!/usr/bin/env bash
# Posts this build's hub-status.json to the Hub after `mvnw ... test` (or `verify`) has
# already run and produced JaCoCo + Surefire output. docs/contracts/hub-status.schema.json
# is the contract; docs/contracts/README.md's P3-1 explains what each field is filled from.
#
# Copy this file and hub_status_payload.py into the target repo (e.g. as ci/hub-report.sh,
# ci/hub_status_payload.py) and add one step after the build/test step of its CI workflow:
#
#   - name: Report to Hub
#     if: always()
#     continue-on-error: true
#     env:
#       HUB_URL: ${{ secrets.HUB_URL }}
#       HUB_CI_TOKEN: ${{ secrets.HUB_CI_TOKEN }}
#       COMPONENT_KEY: <app_group.key>/<component.role>/<component.name>
#     run: ci/hub-report.sh
#
# `continue-on-error: true` matches this file's own workflow's existing coverage-upload
# step: a Hub outage or a not-yet-minted token must never fail a real build/deploy over a
# status report. `HUB_CI_TOKEN` is minted once via
# POST /api/v1/components/{id}/tokens (session-cookie auth, from the Hub UI or curl) and
# stored as a repo secret — plaintext is shown exactly once at creation.
#
# Required env:
#   HUB_URL          e.g. https://hub.milkyway.test  (no trailing /api/... needed)
#   HUB_CI_TOKEN      component bearer token
#   COMPONENT_KEY     e.g. andromeda/auth/andromeda-authorization-server
# Optional env:
#   HUB_ENVIRONMENT   default: test
#   HUB_CACERT        path to a CA bundle for curl to trust, if HUB_URL's own cert is not
#                      already trusted system-wide (milkyway.test's is self-signed) --
#                      passed as --cacert, never -k; default: verify with the system store
#   POM_FILE          default: pom.xml
#   JACOCO_XML        default: target/site/jacoco/jacoco.xml
#   SUREFIRE_DIR      default: target/surefire-reports
#
# P5-1: also reports whether a README exists at the repo root (checked in bash, right
# below, before the report is built) as checklist_item(check_key=documentation) -- no env
# var to configure it, the check is the same everywhere.

set -euo pipefail

: "${HUB_URL:?HUB_URL is required}"
: "${HUB_CI_TOKEN:?HUB_CI_TOKEN is required}"
: "${COMPONENT_KEY:?COMPONENT_KEY is required}"

ENVIRONMENT="${HUB_ENVIRONMENT:-test}"
POM_FILE="${POM_FILE:-pom.xml}"
JACOCO_XML="${JACOCO_XML:-target/site/jacoco/jacoco.xml}"
SUREFIRE_DIR="${SUREFIRE_DIR:-target/surefire-reports}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

COMMIT_SHA="$(git rev-parse HEAD)"
REPORTED_AT="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

BUILD_URL=""
if [ -n "${GITHUB_SERVER_URL:-}" ] && [ -n "${GITHUB_REPOSITORY:-}" ] && [ -n "${GITHUB_RUN_ID:-}" ]; then
  BUILD_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
fi

# P5-1: a README either exists at the repo root or it does not, checked the same
# stack-agnostic way regardless of what this template builds -- unlike coverage, always
# knowable, so always reported (never omitted the way `tests` can be). Checks the current
# working directory first, then the repo root -- a monorepo split
# (hub-backend's own `backend/` working-directory, element-editor's `frontend`/`backend`)
# keeps its one real README at the top, not inside the subdirectory this step runs in;
# checking only the cwd would false-negative every one of those (confirmed against
# hub-backend itself, 2026-09-14: backend/README.md does not exist, the real one is one
# level up).
DOCUMENTATION_PRESENT="false"
REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || echo .)"
for dir in "." "${REPO_ROOT}"; do
  for candidate in README.md README.rst README.txt README; do
    if [ -f "${dir}/${candidate}" ]; then
      DOCUMENTATION_PRESENT="true"
      break 2
    fi
  done
done

PAYLOAD="$(python3 "${SCRIPT_DIR}/hub_status_payload.py" \
  --pom "${POM_FILE}" \
  --jacoco "${JACOCO_XML}" \
  --surefire-dir "${SUREFIRE_DIR}" \
  --component-key "${COMPONENT_KEY}" \
  --environment "${ENVIRONMENT}" \
  --commit-sha "${COMMIT_SHA}" \
  --build-url "${BUILD_URL}" \
  --documentation-present "${DOCUMENTATION_PRESENT}" \
  --reported-at "${REPORTED_AT}")"

echo "==> Reporting to ${HUB_URL%/}/api/v1/reports: ${PAYLOAD}"

CACERT_ARGS=()
if [ -n "${HUB_CACERT:-}" ]; then
  CACERT_ARGS=(--cacert "${HUB_CACERT}")
fi

curl -sS --fail-with-body "${CACERT_ARGS[@]}" -X POST "${HUB_URL%/}/api/v1/reports" \
  -H "Authorization: Bearer ${HUB_CI_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "${PAYLOAD}"
echo
