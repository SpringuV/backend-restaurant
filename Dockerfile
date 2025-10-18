# ===== Stage 1: Build app với Gradle =====
FROM gradle:8.7-jdk21 AS builder

# Tạo thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Cấp quyền thực thi cho gradlew
RUN chmod +x ./gradlew

# Build ứng dụng, bỏ qua test cho nhanh
RUN ./gradlew clean bootJar -x test

# ===== Stage 2: Run app bằng JDK nhẹ =====
FROM openjdk:21-jdk-slim

# Tạo thư mục làm việc
WORKDIR /app

# Copy file jar từ stage build sang
# ⚠️ đường dẫn JAR đúng là build/libs/*.jar (không cần "noodle.restaurant")
COPY --from=builder /app/build/libs/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
