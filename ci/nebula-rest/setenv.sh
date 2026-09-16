#!/bin/bash
# Identical to infrastructure/docker/test/tomcat/setenv/nebula/setenv.sh,
# minus the test-only milkyway.crt keytool import (production uses the cluster
# CA / public trust store; the internal Andromeda call is plain HTTP).
# Kept as a near byte-for-byte copy so the properties file format created by
# ansible/playbooks/templates/nebula-rest.properties.j2 does not need to
# diverge from the test environment's nebula-rest.properties.

PROPERTIES_FILE="/usr/local/tomcat/conf/nebula-rest.properties"

if [ -f "$PROPERTIES_FILE" ]; then
  echo "Found properties file: $PROPERTIES_FILE. Processing..."
  while IFS='=' read -r key value; do
    if [[ ! "$key" =~ ^\s*# && -n "$key" ]]; then
      key=$(echo "$key" | tr -d '[:space:]')
      CATALINA_OPTS="$CATALINA_OPTS \"-D$key=$value\""
    fi
  done < "$PROPERTIES_FILE"

  eval "export CATALINA_OPTS=\"$CATALINA_OPTS\""

  echo "CATALINA_OPTS after processing: $CATALINA_OPTS"
else
  echo "WARNING: Properties file not found: $PROPERTIES_FILE"
fi
export CATALINA_OPTS="$CATALINA_OPTS -Dserver.servlet.context-path=/api"
