# 1단계: 빌드 환경 (Build Stage)
FROM gradle:8-jdk21 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시를 활용하기 위해 gradle wrapper 파일과 build.gradle 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 빌드된 JAR 파일 확인
RUN find . -name "*.jar" -type f

# 2단계: 경량화된 실행 환경 (Production Stage)
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

