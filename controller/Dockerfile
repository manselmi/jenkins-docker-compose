FROM docker.io/jenkins/jenkins:2.486-rhel-ubi9-jdk21
LABEL maintainer="Mike Anselmi <git@manselmi.com>"

# Define TLS-related environment variables.
ENV TLS_CA_BUNDLE_JKS=/etc/pki/ca-trust/extracted/java/cacerts \
    TLS_CA_BUNDLE_PEM=/etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem
ENV CURL_CA_BUNDLE="${TLS_CA_BUNDLE_PEM}" \
    SSL_CERT_FILE="${TLS_CA_BUNDLE_PEM}"

# Update certificate authority anchors.
USER root
COPY ["resources/tls-ca-bundle.pem", "/usr/share/pki/ca-trust-source/anchors/tls-ca-bundle.pem"]
RUN update-ca-trust extract
USER jenkins

# Install configuration script and plugins.
COPY ["resources/controller/setup.groovy", "/usr/share/jenkins/ref/init.groovy.d/setup.groovy"]
RUN jenkins-plugin-cli --plugins \
      basic-branch-build-strategies \
      blueocean \
      credentials-binding \
      docker-plugin \
      git \
      github-branch-source \
      matrix-auth \
      ws-cleanup
