# 백엔드 자동 배포 가이드

이 문서는 백엔드 프로젝트의 자동 배포 설정을 위한 가이드입니다.

## 목차
1. [사전 요구사항](#사전-요구사항)
2. [GitHub Secrets 설정](#github-secrets-설정)
3. [EC2 서버 설정](#ec2-서버-설정)
4. [첫 배포 테스트](#첫-배포-테스트)
5. [문제 해결](#문제-해결)

## 사전 요구사항

- GitHub 저장소에 코드가 푸시되어 있어야 합니다
- DockerHub 계정이 있어야 합니다
- EC2 서버에 Docker와 Docker Compose가 설치되어 있어야 합니다
- EC2 서버에 SSH 접근이 가능해야 합니다

## GitHub Secrets 설정

GitHub 저장소의 Settings → Secrets and variables → Actions에서 다음 Secrets를 설정해야 합니다.

### 필수 Secrets

1. **DOCKER_USERNAME**
   - 설명: DockerHub 사용자명
   - 예시: `myusername`

2. **DOCKER_PASSWORD**
   - 설명: DockerHub 비밀번호 또는 Personal Access Token
   - 보안: DockerHub에서 Personal Access Token 사용을 권장합니다
   - 생성 방법:
     1. DockerHub 로그인
     2. Account Settings → Security → New Access Token
     3. 토큰 생성 후 복사하여 Secrets에 등록

3. **EC2_HOST**
   - 설명: EC2 서버의 IP 주소 또는 도메인
   - 예시: `123.456.789.012` 또는 `api.example.com`

4. **SSH_PRIVATE_KEY**
   - 설명: EC2 서버 접속용 SSH 개인키
   - 형식: OpenSSH 형식의 개인키 전체 내용
   - 예시:
     ```
     -----BEGIN OPENSSH PRIVATE KEY-----
     b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAACFwAAAAdzc2gtcn
     ...
     -----END OPENSSH PRIVATE KEY-----
     ```
   - 주의: 공개키는 EC2 서버의 `~/.ssh/authorized_keys`에 등록되어 있어야 합니다

### Secrets 설정 방법

1. GitHub 저장소로 이동
2. Settings → Secrets and variables → Actions 클릭
3. "New repository secret" 버튼 클릭
4. Name과 Secret 값 입력
5. "Add secret" 버튼 클릭

**참고**: 프론트엔드 프로젝트와 동일한 Secrets를 사용할 수 있습니다.

## EC2 서버 설정

### 1. 디렉토리 생성

EC2 서버에 SSH로 접속한 후 다음 명령어를 실행합니다:

```bash
mkdir -p /home/ec2-user/backend
cd /home/ec2-user/backend
```

### 2. docker-compose.yml 파일 생성

`docker-compose.template.yml` 파일을 참고하여 EC2 서버에 맞게 수정한 `docker-compose.yml` 파일을 생성합니다:

```bash
nano docker-compose.yml
# 또는
vi docker-compose.yml
```

주요 수정 사항:
- 데이터베이스 연결 정보 (호스트, 포트, 데이터베이스명, 사용자명, 비밀번호)
- JWT 시크릿 키
- 포트 매핑 (필요한 경우)
- 네트워크 설정 (프론트엔드와 통신해야 하는 경우)

### 3. .env 파일 생성

Docker Compose에서 사용할 환경 변수를 설정합니다:

```bash
nano .env
```

다음 내용을 추가합니다:

```bash
DOCKER_REGISTRY_USER=your-dockerhub-username
```

### 4. Docker 네트워크 생성 (필요한 경우)

프론트엔드와 같은 네트워크를 사용하려면:

```bash
docker network create app-network
```

또는 프론트엔드에서 이미 생성한 네트워크가 있다면 그 이름을 확인하여 사용합니다.

### 5. 초기 서비스 시작 (선택사항)

첫 배포 전에 수동으로 서비스를 시작하여 설정이 올바른지 확인할 수 있습니다:

```bash
docker compose up -d backend-dev
docker compose logs -f backend-dev
```

## 첫 배포 테스트

### 1. develop 브랜치에 푸시

```bash
git checkout develop
git add .
git commit -m "배포 테스트"
git push origin develop
```

### 2. GitHub Actions 로그 확인

1. GitHub 저장소로 이동
2. Actions 탭 클릭
3. 실행 중인 워크플로우 클릭
4. 각 단계의 로그 확인

### 3. EC2 서버에서 확인

SSH로 EC2 서버에 접속하여 다음 명령어로 확인:

```bash
# 컨테이너 상태 확인
docker ps

# 특정 서비스 로그 확인
docker logs backend-dev

# 실시간 로그 확인
docker logs -f backend-dev

# 서비스 재시작 (필요한 경우)
cd /home/ec2-user/backend
docker compose restart backend-dev
```

### 4. 애플리케이션 동작 확인

- API 엔드포인트 호출 테스트
- 데이터베이스 연결 확인
- 로그에서 에러 메시지 확인

## 프로덕션 배포

develop 브랜치 배포가 성공적으로 완료된 후:

### 1. master 브랜치에 푸시

```bash
git checkout master
git merge develop  # 또는 develop의 변경사항을 master에 반영
git push origin master
```

### 2. 배포 확인

develop 배포와 동일한 방법으로 확인:

```bash
docker ps
docker logs backend-prod
```

## 문제 해결

### 배포 실패 시

1. **GitHub Actions 로그 확인**
   - 빌드 실패: Dockerfile 또는 코드 문제
   - 푸시 실패: DockerHub 인증 문제
   - SSH 접속 실패: SSH_PRIVATE_KEY 또는 EC2_HOST 확인
   - 배포 스크립트 실패: EC2 서버의 docker-compose.yml 확인

2. **EC2 서버에서 직접 확인**
   ```bash
   # Docker 이미지 확인
   docker images | grep joyopi-backend
   
   # 컨테이너 상태 확인
   docker ps -a
   
   # 로그 확인
   docker logs backend-dev
   docker logs backend-prod
   ```

3. **일반적인 문제들**
   - **포트 충돌**: 다른 서비스가 같은 포트를 사용 중인지 확인
   - **네트워크 문제**: `app-network`가 존재하는지 확인
   - **환경 변수 누락**: docker-compose.yml의 environment 섹션 확인
   - **권한 문제**: EC2 사용자가 docker 명령어를 실행할 수 있는지 확인

### 로그 확인 명령어

```bash
# 모든 서비스 로그
docker compose logs

# 특정 서비스 로그
docker compose logs backend-dev
docker compose logs backend-prod

# 실시간 로그
docker compose logs -f backend-dev

# 최근 100줄만 보기
docker compose logs --tail=100 backend-dev
```

### 서비스 재시작

```bash
cd /home/ec2-user/backend

# 특정 서비스 재시작
docker compose restart backend-dev

# 특정 서비스 중지 및 시작
docker compose stop backend-dev
docker compose start backend-dev

# 컨테이너 재생성
docker compose up -d --force-recreate backend-dev
```

## 추가 참고사항

- 프론트엔드와 동일한 DockerHub 계정 사용
- 데이터베이스 연결 정보 등 민감한 정보는 환경 변수로 관리
- 필요시 Nginx 리버스 프록시 설정 확인
- 정기적으로 사용하지 않는 Docker 이미지 정리: `docker system prune -a`

