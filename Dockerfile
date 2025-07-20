# Java 17 환경을 기반으로 하는 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder
WORKDIR /workspace/app

# Gradle 래퍼와 소스코드 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# 실행 권한 부여 및 Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 실제 실행을 위한 경량 이미지 스테이지
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사
COPY --from=builder /workspace/app/build/libs/app.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]