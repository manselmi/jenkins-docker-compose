``` shell
docker image build --file ./controller/Dockerfile --tag jenkins-controller:latest -- .
docker image build --file ./agents/manylinux_2_28_aarch64/Dockerfile --tag jenkins-agent-manylinux_2_28_aarch64:latest -- .

docker-compose up
# play around with Jenkins at http://localhost:4080 (see .env file for login credentials)
docker-compose down

# cleanup
docker volume rm -- jenkins-controller
```
