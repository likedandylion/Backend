# Mac (ARM64/M1/M2/M3) 기반
FROM eclipse-temurin:21-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 JAR 파일 복사
COPY build/libs/*.jar prome.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "prome.jar"]