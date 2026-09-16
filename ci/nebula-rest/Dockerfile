# To be copied into the nebula-rest-api repository as
# ci/nebula-rest/Dockerfile (referenced by .github/workflows/deploy-prod.yml).
#
# Ported from infrastructure/docker/test/tomcat/image/nebula/Dockerfile and
# the Andromeda production pattern in ci-templates/andromeda/Dockerfile.
# Test bind-mounts the WAR/setenv.sh/properties file into a stock
# tomcat:10.1-jdk21-temurin container; Kubernetes has no host bind mount, so
# the WAR and setenv.sh are baked into the image instead. The properties file
# stays out of the image — it is delivered separately as a Kubernetes Secret
# by ansible/playbooks/06-nebula-secret.yml and mounted at the same path
# the setenv.sh script already expects.
FROM tomcat:10.1-jdk21-temurin

ARG WAR_FILE

COPY target/${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war
COPY ci/nebula-rest/setenv.sh /usr/local/tomcat/bin/setenv.sh

RUN chmod +x /usr/local/tomcat/bin/setenv.sh
