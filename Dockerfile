# ----------------------------------------------------------------------
# GIAI ĐOẠN 1: BUILD - Dùng image JDK để biên dịch mã nguồn và tạo JAR
# ----------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-focal AS builder

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Copy các file cần thiết cho quá trình Build Maven/Spring Boot:
# 1. pom.xml (để tải dependencies)
# 2. mvnw (Maven Wrapper script)
# 3. .mvn (Thư mục cấu hình của Maven Wrapper)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn/
COPY src ./src

# Cấp quyền thực thi cho Maven Wrapper script
RUN chmod +x mvnw

# Chạy lệnh build Maven để tạo ra file JAR (bỏ qua Test để build nhanh hơn)
RUN ./mvnw clean package -DskipTests

# ----------------------------------------------------------------------
# GIAI ĐOẠN 2: RUNTIME - Dùng image JRE nhỏ gọn để chạy ứng dụng
# ----------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

# Thiết lập thư mục làm việc
WORKDIR /

# Copy file JAR từ giai đoạn "builder" sang image runtime
# Sử dụng wildcard (*) để đảm bảo tên file JAR luôn đúng, và đổi tên thành app.jar
COPY --from=builder /app/target/*.jar app.jar

# Mở cổng 8080 (cổng chạy ứng dụng Spring Boot)
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
# Lỗi cổng cũ được giải quyết bằng cách dùng biến môi trường trong docker-compose.yml
ENTRYPOINT ["java","-jar","/app.jar"]