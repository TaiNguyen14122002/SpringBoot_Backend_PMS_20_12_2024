FROM maven:3-openjdk-17 AS build
WORKDIR /app

# Copy tất cả mã nguồn và build ứng dụng
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Sao chép file .jar từ build stage vào container
COPY --from=build /app/target/ProjectManagementSystems-0.0.1-SNAPSHOT.jar ProjectManagementSystems.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "ProjectManagementSystems.jar"]
