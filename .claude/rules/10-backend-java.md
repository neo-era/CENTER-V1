# Luật: Backend Java / Spring Boot

## Phiên bản & build
- **Java 21** (dùng record, sealed, pattern matching, virtual threads khi hợp lý).
- **Spring Boot 3.3+**, build bằng **Maven** multi-module. Version quản lý tập trung trong parent `pom.xml` (dependencyManagement/BOM). KHÔNG ghi version rời trong module con.

## Cấu trúc package
`vn.center.<service>` → `api` (controller/dto) · `domain` (entity/model) · `service` (logic) · `repo` · `config` · `messaging` (Kafka). Không để logic nghiệp vụ trong controller.

## Quy ước code
- Ưu tiên **constructor injection** (không `@Autowired` field). Dùng `final`.
- DTO là **record** bất biến; KHÔNG lộ entity JPA ra API.
- Validate input bằng Bean Validation (`@Valid`, `jakarta.validation`). Mọi input ngoài đều coi là không tin cậy.
- Lỗi: `@RestControllerAdvice` tập trung → trả về theo **RFC 7807 (Problem Details)**. Không nuốt exception, không lộ stack trace ra client.
- Log có cấu trúc (JSON, SLF4J + Logback). KHÔNG log dữ liệu nhạy cảm/secret/PII thô.
- Thời gian: dùng `Instant`/`OffsetDateTime` UTC trong hệ thống; quy đổi sang giờ VN (Asia/Ho_Chi_Minh) ở lớp trình bày.
- Tiền/chỉ số điện: `BigDecimal` cho tính toán tích lũy (kWh); `double`/`float` chỉ cho giá trị đo tức thời.

## Concurrency & hiệu năng
- I/O nhiều kết nối (gateway, ingestion): dùng WebFlux reactive hoặc virtual threads.
- Tránh chặn luồng trong reactive chain. Dùng connection pool (HikariCP) hợp lý.
- Idempotency cho consumer Kafka (lệnh điều khiển không được thực thi 2 lần).

## Cấu hình
- Cấu hình qua `application.yml` + biến môi trường (12-factor). Secret từ Vault, KHÔNG để trong yml/commit.
- Mỗi service có `/actuator/health`, `/actuator/prometheus` (Micrometer + OpenTelemetry).

## Chất lượng
- Format/lint: Spotless + Checkstyle (Google style hoặc cấu hình dự án). Build fail nếu vi phạm.
- Không commit code không qua `mvn verify`.
