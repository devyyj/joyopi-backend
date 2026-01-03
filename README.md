# Joyopi Backend

Spring Boot 기반 백엔드 애플리케이션입니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.0
- Gradle

## 실행 방법

```bash
./gradlew bootRun
```

## 빌드 방법

```bash
./gradlew build
```

## 배포 및 설정

이 프로젝트는 GitHub Actions를 통해 EC2 서버로 자동 배포됩니다.

### GitHub Secrets 설정

GitHub 저장소의 `Settings` → `Secrets and variables` → `Actions`에서 다음 항목을 설정해야 합니다:
- `DOCKER_USERNAME`: DockerHub 사용자명
- `DOCKER_PASSWORD`: DockerHub 비밀번호 또는 Access Token
- `EC2_HOST`: EC2 서버 IP 주소
- `SSH_PRIVATE_KEY`: EC2 접속용 SSH 개인키

### 서버 설정

1. EC2 서버의 `/home/ec2-user/backend` 디렉토리에 `docker-compose.yml`을 배치합니다.
2. `.env` 파일에 `DOCKER_REGISTRY_USER`를 설정합니다.
3. `docker network create app-network` 명령어로 네트워크를 생성합니다. (프론트엔드와 공유 시 필수)
