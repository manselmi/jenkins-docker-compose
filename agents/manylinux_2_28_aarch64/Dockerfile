# derived from:
#   https://github.com/jenkinsci/docker-agent

FROM quay.io/pypa/manylinux_2_28_aarch64:latest
LABEL maintainer="Mike Anselmi <git@manselmi.com>"

# Define TLS-related environment variables.
ENV TLS_CA_BUNDLE_JKS=/etc/pki/ca-trust/extracted/java/cacerts \
    TLS_CA_BUNDLE_PEM=/etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem
ENV AWS_CA_BUNDLE="${TLS_CA_BUNDLE_PEM}" \
    CURL_CA_BUNDLE="${TLS_CA_BUNDLE_PEM}" \
    PIP_CERT="${TLS_CA_BUNDLE_PEM}" \
    REQUESTS_CA_BUNDLE="${TLS_CA_BUNDLE_PEM}" \
    SSL_CERT_FILE="${TLS_CA_BUNDLE_PEM}" \
    TWINE_CERT="${TLS_CA_BUNDLE_PEM}"

# Update certificate authority anchors.
COPY ["resources/tls-ca-bundle.pem", "/usr/share/pki/ca-trust-source/anchors/tls-ca-bundle.pem"]
RUN update-ca-trust extract

# Configure Jenkins agent (suitable for Jenkins Docker plugin).
ARG remoting_version=3261.v9c670a_4748a_9
ARG gid=1000
ARG group=jenkins
ARG uid=1000
ARG user=jenkins
ARG home="/home/${user}"
ENV JENKINS_AGENT_WORKDIR="${home}/agent"
COPY ["resources/agents/jenkins-agent", "/usr/local/bin/jenkins-agent"]
RUN dnf --assumeyes --setopt=install_weak_deps=False -- install \
      git \
      java-21-openjdk-headless \
    && dnf clean all \
    && rm --force --recursive -- /var/cache/dnf \
    && groupadd --gid "${gid}" -- "${group}" \
    && useradd \
         --create-home \
         --gid "${gid}" \
         --home-dir "${home}" \
         --system \
         --uid "${uid}" \
         -- \
         "${user}" \
    && curl \
         --create-dirs \
         --fail \
         --location \
         --max-time 60 \
         --output /usr/share/jenkins/agent.jar \
         --show-error \
         --silent \
         -- \
         "https://repo.jenkins-ci.org/artifactory/public/org/jenkins-ci/main/remoting/${remoting_version}/remoting-${remoting_version}.jar" \
    && chmod 755 -- /usr/share/jenkins \
    && chmod 644 -- /usr/share/jenkins/agent.jar
USER "${user}"
RUN mkdir --parents -- "${JENKINS_AGENT_WORKDIR}" "${home}/.jenkins"
ENTRYPOINT ["/usr/local/bin/jenkins-agent"]
WORKDIR "${JENKINS_AGENT_WORKDIR}"

# Prepend supported Python binary directories to PATH (most recent first).
#
# Status of Python versions: https://devguide.python.org/versions/#versions
ENV PATH="/home/${user}/.local/bin:/opt/python/cp313-cp313/bin:/opt/python/cp312-cp312/bin:/opt/python/cp311-cp311/bin:/opt/python/cp310-cp310/bin:/opt/python/cp39-cp39/bin:${PATH}"

# Install baseline Python requirements.
RUN python -m pip --no-cache-dir install --upgrade -- pip setuptools wheel \
    && python -m pip --no-cache-dir install --upgrade -- packaging pkginfo pre-commit tox twine \
    && python -m pip check \
    && PYTHONS='python3.13 python3.12 python3.11 python3.10 python3.9' \
    && for PYTHON in ${PYTHONS} ; do \
         "${PYTHON}" -m pip --no-cache-dir install --upgrade -- pip setuptools wheel \
         && "${PYTHON}" -m pip --no-cache-dir install --upgrade -- build \
         && "${PYTHON}" -m pip check ; \
       done

# Configure Jenkins agent (suitable for auto-registration with a Jenkins controller via JNLP as a
# permanent agent).
USER root
ARG venv_dir=/venv
ARG jenkins_agent_auto_launcher_venv_dir="${venv_dir}/jenkins-agent-auto"
ARG jenkins_agent_auto_launcher_python="${jenkins_agent_auto_launcher_venv_dir}/bin/python"
ARG jenkins_agent_auto_launcher_requirements="${venv_dir}/requirements.txt"
ARG jenkins_agent_auto_launcher=/usr/local/bin/jenkins-agent-auto
COPY ["resources/agents/jenkins-agent-auto-requirements.txt", "${jenkins_agent_auto_launcher_requirements}"]
COPY ["resources/agents/jenkins-agent-auto", "${jenkins_agent_auto_launcher}"]
RUN /opt/python/cp313-cp313/bin/python -m venv -- "${jenkins_agent_auto_launcher_venv_dir}" \
    && "${jenkins_agent_auto_launcher_python}" -m pip --no-cache-dir install --upgrade -- pip setuptools wheel \
    && "${jenkins_agent_auto_launcher_python}" -m pip --no-cache-dir install --no-deps --requirement="${jenkins_agent_auto_launcher_requirements}" \
    && "${jenkins_agent_auto_launcher_python}" -m pip check \
    && rm --force -- "${jenkins_agent_auto_launcher_requirements}"
USER "${user}"
