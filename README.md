# Jenkins (Docker Compose)

Spin up a simple Jenkins environment using Docker compose.  See [`controller`](controller) for the
Jenkins controller and [`agents`](agents) for Jenkins agents.  Agents automatically register
themselves with the controller.

## Instructions

Build controller and agent container images:

``` shell
docker image build --file ./controller/Dockerfile --tag jenkins-controller:latest -- .
docker image build --file ./agents/manylinux_2_28_aarch64/Dockerfile --tag jenkins-agent-manylinux_2_28_aarch64:latest -- .
```

Bring up the Jenkins environment.

``` shell
docker-compose up
```

Play around with Jenkins at [http://localhost:4080](http://localhost:4080). See the `.env` file for
login credentials.

Bring down the Jenkins environment:

``` shell
docker-compose down
```

Optionally, remove the `jenkins-controller` volume:

``` shell
docker volume rm -- jenkins-controller
```
