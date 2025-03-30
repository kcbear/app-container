# Pre-requisite for Apple Silicon MacOS

https://github.com/abiosoft/colima#installation

Install colima
Run `colima start --arch x86_64 --memory 4`

Set TestContainers env vars

export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export DOCKER_HOST="unix://${HOME}/.colima/docker.sock"
Run your tests based on Gerald Venzl's Oracle XE image