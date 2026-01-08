# 🏨 Travel & Hotel Booking Management System (Backend)

![Project Status](https://img.shields.io/badge/status-active-success.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green.svg?logo=springboot)
![Java](https://img.shields.io/badge/Java-17%2B-orange.svg?logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache-red.svg?logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange.svg?logo=rabbitmq)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

> **Môn học:** Project 3  
> **Giảng viên hướng dẫn:** [Tên Giảng Viên]  
> **Sinh viên thực hiện:** Quách Hải Linh  
> **MSSV:** 20225206  
> **Trường:** Đại học Bách Khoa Hà Nội (HUST)

---

## 📖 Giới thiệu (Introduction)

Đây là **Backend Server** cho hệ thống quản lý đặt phòng khách sạn và tour du lịch. Hệ thống cung cấp nền tảng toàn diện cho phép người dùng tìm kiếm, đặt phòng, đặt tour và quản lý các dịch vụ du lịch. Dự án được xây dựng theo kiến trúc Microservices (hoặc Monolithic tùy thực tế) chú trọng vào hiệu năng cao và khả năng mở rộng.

Hệ thống hỗ trợ các tính năng thời gian thực (Real-time) như thông báo và chat, đồng thời xử lý các tác vụ bất đồng bộ để tối ưu hóa trải nghiệm người dùng.

## 🛠️ Công nghệ sử dụng (Tech Stack)

Dự án sử dụng các công nghệ hiện đại và mạnh mẽ nhất trong hệ sinh thái Java:

| Công nghệ | Mục đích sử dụng |
| :--- | :--- |
| **Spring Boot** | Framework chính để phát triển RESTful API. |
| **Spring Security & JWT** | Xác thực và phân quyền người dùng (Authentication & Authorization). |
| **PostgreSQL** | Cơ sở dữ liệu quan hệ lưu trữ thông tin người dùng, khách sạn, tour, booking. |
| **Redis** | Caching dữ liệu hay truy xuất (Tour, Room) để tăng tốc độ phản hồi API. |
| **RabbitMQ** | Message Broker xử lý các tác vụ bất đồng bộ (Gửi email xác nhận, xử lý thanh toán nền). |
| **WebSocket (STOMP)** | Giao tiếp thời gian thực (Real-time notification, Chat support). |
| **Render** | Nền tảng Cloud dùng để Deploy Backend Server. |
| **Ngrok** | Tunneling phục vụ development và demo local ra internet. |

## 🚀 Tính năng chính (Key Features)

* **Quản lý người dùng:** Đăng ký, đăng nhập, quên mật khẩu, cập nhật hồ sơ.
* **Quản lý Khách sạn & Phòng:**
    * CRUD khách sạn, loại phòng.
    * Tìm kiếm và lọc nâng cao (theo giá, địa điểm, tiện ích).
* **Quản lý Tour du lịch:**
    * Lên lịch trình, quản lý slot, hướng dẫn viên.
* **Booking Engine:**
    * Xử lý đặt phòng/tour với transaction đảm bảo tính toàn vẹn dữ liệu.
    * Kiểm tra tình trạng phòng trống (Availability check).
* **Hệ thống thông báo (Real-time):**
    * Thông báo khi đặt thành công hoặc hủy qua WebSocket.
* **Đánh giá & Bình luận:** Cho phép người dùng review dịch vụ.

## ⚙️ Kiến trúc hệ thống (System Architecture)

Luồng dữ liệu cơ bản:
1.  Client gửi Request -> **Spring Boot Controller**.
2.  Kiểm tra Cache tại **Redis** (nếu có -> trả về).
3.  Nếu không, truy vấn **PostgreSQL** -> lưu Cache -> trả về.
4.  Khi Booking thành công -> Đẩy message vào **RabbitMQ**.
5.  Consumer nhận message -> Gửi Email + Bắn **WebSocket** noti về Client.

## 🔧 Cài đặt và Chạy ứng dụng (Installation)

### Yêu cầu tiên quyết (Prerequisites)
* Java Development Kit (JDK) 17 trở lên.
* Maven 3.x.
* Docker & Docker Compose (để chạy Redis, PostgreSQL, RabbitMQ nhanh chóng).

### Các bước cài đặt

1.  **Clone repository:**
    ```bash
    git clone https://github.com/bestzedcoder/backend-booking-tour-hotel.git
    cd backend-booking-tour-hotel
    ```

2.  **Cấu hình môi trường:**
    Tạo file `.env` hoặc chỉnh sửa `src/main/resources/application.yml` với các thông số:
    ```properties
    # Database Configuration
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/travel_db
    SPRING_DATASOURCE_USERNAME=postgres
    SPRING_DATASOURCE_PASSWORD=your_password

    # Redis Configuration
    SPRING_REDIS_HOST=localhost
    SPRING_REDIS_PORT=6379

    # RabbitMQ Configuration
    SPRING_RABBITMQ_HOST=localhost
    SPRING_RABBITMQ_PORT=5672
    ```

3.  **Khởi chạy hạ tầng (bằng Docker):**
    ```bash
    docker-compose up -d
    ```

4.  **Build và chạy ứng dụng:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

## 🌐 API Documentation

Dự án tích hợp **Swagger UI** để test API trực quan.
Sau khi chạy server, truy cập:
* Local: `http://localhost:8080/swagger-ui/index.html`
* Live Demo (Render): `https://your-app-name.onrender.com/swagger-ui/index.html`

## ☁️ Deployment

Dự án hiện đang được deploy tại:
* **Server Host:** [Render](https://render.com)
* **Database Host:** [Render PostgreSQL / Supabase]
* **Dev Tunnel:** Sử dụng **Ngrok** để expose port 8080 cho mục đích testing webhook hoặc demo nhanh.

## 👨‍💻 Tác giả (Author)

**Quách Hải Linh**
* **MSSV:** 20225206
* **Lớp:** IT2-02
* **Email:** Linh.QH225206@sis.hust.edu.vn

---
*Developed with ❤️ for Project 3 - HUST*