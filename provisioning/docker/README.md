# Jenkins

## Jenkins Image 빌드

```shell
#!/bin/bash

# 기본 Jenkins Container 중지 / 삭제
sudo docker stop --time=60 jenkins
sudo docker rm jenkins

cd ( Jenkins Dockerfile 경로 )

# Jenkins Image 빌드 : 빌드 전 기존 이미지 삭제
JENKINS_IMAGE_FULL_URL='nexus.cicd-practice.com/cicd-app/jenkins:2.387.1-lts' && \
sudo docker rmi -f "${JENKINS_IMAGE_FULL_URL}" || true && \
sudo docker build \
  --rm \
  --force-rm \
  --no-cache \
  --tag "${JENKINS_IMAGE_FULL_URL}" \
  -f ./Dockerfile-jenkins \
  .
```

## Jenkins Container 시작

```shell
# WSL 시작시 bridge 모드 docker network 생성
sudo docker network create 'devops-network'

# Stateless Jenkins 구현 : 기존 Jenkins Volume 경로 완전 삭제 후 신규 생성
sudo rm -rf /sw/jenkins
sudo mkdir -p /sw/jenkins
sudo chown -R 1000:1000 /sw/jenkins # docker hub UID

# (최초 Jenkins 기동시만) Jenkins 에서 사용할 Maven / Gradle 의존성 Volume 경로 생성
sudo mkdir -p /sw/dependencies
sudo chown -R 1000:1000 /sw/dependencies

# Jenkins Container 시작
JENKINS_IMAGE_FULL_URL='nexus.cicd-practice.com/cicd-app/jenkins:2.387.1-lts' && \
sudo docker run \
  -d --privileged \
  --name 'jenkins' \
  --net 'devops-network' \
  --dns '8.8.8.8' \
  -v /sw/jenkins:/var/jenkins_home \
  -v /sw/dependencies:/jenkins_dependencies \
  -p 9001:8080 \
  -p 50000:50000 \
  "${JENKINS_IMAGE_FULL_URL}"
```
