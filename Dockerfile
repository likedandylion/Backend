# Windows (AMD64/x86_64) 기반
FROM --platform=linux/amd64 eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 JAR 파일 복사
COPY build/libs/*.jar prome-0.0.1-SNAPSHOT.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "prome-0.0.1-SNAPSHOT.jar"]