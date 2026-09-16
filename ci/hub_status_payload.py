#!/usr/bin/env python3
"""Builds one `hub-status.json` (docs/contracts/hub-status.schema.json) from a Maven
build's own JaCoCo + Surefire output. Called by hub-report.sh in this same template;
kept in Python because parsing three XML shapes in bash is worse than the dependency —
python3 is already a hard requirement of every self-hosted runner in this lab.
"""

from __future__ import annotations

import argparse
import glob
import json
import sys
import xml.etree.ElementTree as ET

POM_NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def read_version(pom_path: str) -> str:
    root = ET.parse(pom_path).getroot()
    version = root.find("m:version", POM_NS)
    if version is None or not version.text:
        # Maven lets a module inherit its version from <parent>; this template only
        # needs the common case where a project declares its own.
        raise SystemExit(f"no <version> element in {pom_path} — does this module inherit it?")
    return version.text.strip()


def read_coverage_percent(jacoco_xml: str) -> float | None:
    try:
        root = ET.parse(jacoco_xml).getroot()
    except (FileNotFoundError, ET.ParseError):
        return None
    for counter in root.findall("counter"):
        if counter.get("type") == "INSTRUCTION":
            covered = int(counter.get("covered", "0"))
            missed = int(counter.get("missed", "0"))
            total = covered + missed
            return round(covered / total * 100, 2) if total else None
    return None


def read_test_counts(surefire_dir: str) -> tuple[int, int]:
    passed = 0
    failed = 0
    for path in glob.glob(f"{surefire_dir}/TEST-*.xml"):
        root = ET.parse(path).getroot()
        tests = int(root.get("tests", "0"))
        failures = int(root.get("failures", "0"))
        errors = int(root.get("errors", "0"))
        skipped = int(root.get("skipped", "0"))
        failed += failures + errors
        passed += tests - failures - errors - skipped
    return passed, failed


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pom", required=True)
    parser.add_argument("--jacoco", required=True)
    parser.add_argument("--surefire-dir", required=True)
    parser.add_argument("--component-key", required=True)
    parser.add_argument("--environment", required=True)
    parser.add_argument("--commit-sha", required=True)
    parser.add_argument("--build-url", default="")
    parser.add_argument("--reported-at", required=True)
    # P5-1: always known (hub-report.sh checks for the file itself, in bash, before
    # calling this script), so this is required rather than optional like --build-url.
    parser.add_argument("--documentation-present", required=True, choices=["true", "false"])
    args = parser.parse_args()

    passed, failed = read_test_counts(args.surefire_dir)
    coverage_percent = read_coverage_percent(args.jacoco)
    payload = {
        "schema_version": 1,
        "component_key": args.component_key,
        "environment": args.environment,
        "version": read_version(args.pom),
        "commit_sha": args.commit_sha,
        "build_url": args.build_url or None,
        "documentation": {"present": args.documentation_present == "true"},
        "reported_at": args.reported_at,
    }
    # A build that skipped tests (e.g. a -DskipTests prod deploy) has nothing to say about
    # coverage; omitting `tests` entirely writes only deployment.version, on
    # hub-status.schema.json's own "not forced to say something anyway" rule -- sending
    # passed=0/failed=0 instead would misreport "ran and found nothing" as "never ran".
    if passed or failed or coverage_percent is not None:
        payload["tests"] = {
            "passed": passed,
            "failed": failed,
            "coverage_percent": coverage_percent,
        }
    json.dump(payload, sys.stdout)


if __name__ == "__main__":
    main()
