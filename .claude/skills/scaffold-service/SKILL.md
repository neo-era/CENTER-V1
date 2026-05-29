---
name: scaffold-service
description: Tạo một microservice Spring Boot mới đúng chuẩn CENTER-V1 (cấu trúc package, pom con, actuator, Kafka, test Testcontainers). Dùng khi cần thêm một service backend mới vào services/.
---

# Skill: Tạo microservice mới

Mục tiêu: thêm service backend nhất quán với kiến trúc (rules/00) và chuẩn Java (rules/10).

## Bước 1 — Xác nhận trước khi tạo
- Service thuộc bounded context nào? Đối chiếu bảng service trong [docs/architecture/overview.md](../../../docs/architecture/overview.md). KHÔNG tạo service ôm nhiều domain.
- Nó sở hữu dữ liệu gì (DB riêng)? Tiêu thụ/phát event Kafka nào?
- Nếu là quyết định kiến trúc mới (đổi ranh giới) → chạy skill `new-adr` trước.

## Bước 2 — Tạo module Maven
1. Tạo thư mục `services/<ten-service>/`.
2. `pom.xml` con: parent = `vn.center:center-parent`, **không** khai version dependency (đã ở parent BOM).
3. Khai báo module trong `services/pom.xml` (`<modules>`).
4. Cấu trúc package `vn.center.<ten>`:
   ```
   api/        # controller + record DTO (không lộ entity)
   domain/     # entity/model
   service/    # logic nghiệp vụ
   repo/       # repository
   messaging/  # Kafka producer/consumer (event canonical từ libs/events)
   config/     # cấu hình, security
   ```
5. `application.yml`: cấu hình qua biến môi trường; bật `/actuator/health` + `/actuator/prometheus`; secret từ Vault (không ghi cứng).

## Bước 3 — Chuẩn bắt buộc kèm theo
- Bean Validation cho mọi input; `@RestControllerAdvice` trả RFC 7807.
- OpenAPI (springdoc) bật sẵn.
- Bảo mật: resource server OIDC (Keycloak), RBAC theo scope (rules/70).
- Nếu có hành động thay đổi → phát `AuditEvent`.
- Test: ít nhất 1 unit + 1 integration (Testcontainers) chạy xanh.
- Migration Flyway nếu có schema.

## Bước 4 — Hoàn tất
- `cd services && mvn -q -pl <ten-service> -am verify` xanh.
- Thêm service vào `docker-compose.dev.yml` / Helm nếu cần chạy chung.
- Cập nhật bảng service trong overview.md + sơ đồ.

## Tham chiếu luật
rules/00-architecture, rules/10-backend-java, rules/30-api-design, rules/40-database, rules/70-security, rules/80-testing.
