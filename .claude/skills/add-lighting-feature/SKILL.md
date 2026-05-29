---
name: add-lighting-feature
description: Quy trình hiện thực đầu-cuối một tính năng nghiệp vụ (mã F-xxx) của CENTER-V1 — từ yêu cầu, hợp đồng API, backend, web, test đến i18n và cập nhật ma trận tính năng. Dùng khi triển khai bất kỳ tính năng F-xxx hoặc F-Txx nào.
---

# Skill: Hiện thực một tính năng F-xxx

Áp dụng cho mọi tính năng trong [feature-matrix.md](../../../docs/requirements/feature-matrix.md). Bám Definition of Done ở [rules/80-testing.md](../../rules/80-testing.md).

## Bước 0 — Hiểu yêu cầu
- Mở feature-matrix, lấy mã `F-xxx`, service phụ trách, mô tả.
- Đọc đoạn yêu cầu gốc tương ứng trong [spec-fulltext.md](../../../docs/requirements/spec-fulltext.md).
- Nếu liên quan thiết bị → đối chiếu [opc-ua-tag-contract.md](../../../docs/architecture/opc-ua-tag-contract.md) xem cần tag nào (skill `opc-ua-tag`).
- ⚠️ Nếu là tín hiệu giao thông (F-Txx) → bắt buộc xét an toàn vận hành: kiểm tra xung đột pha (F-T11), fail-safe (F-T12).

## Bước 1 — Hợp đồng trước
- Cập nhật/đề xuất OpenAPI cho endpoint mới (rules/30). Định nghĩa DTO record.
- Nếu cần event mới → cập nhật schema trong `services/libs/events` (versioned).

## Bước 2 — Dữ liệu
- Migration Flyway (rules/40). Chọn đúng kho: PostgreSQL/PostGIS/TimescaleDB. Telemetry → hypertable + continuous aggregate cho biểu đồ.

## Bước 3 — Backend
- Hiện thực trong service phụ trách (rules/10). Logic ở `service/`, không ở controller.
- RBAC theo chức năng + khu vực (rules/70). Validate input.
- Hành động thay đổi (điều khiển, CRUD) → phát `AuditEvent` (actor, thời điểm, IP, lý do).
- Lệnh xuống tủ → qua Kafka `command.cabinet` → opcua-gateway → xác nhận PV.

## Bước 4 — Frontend
- Web (rules/50): màn hình/biểu đồ/bản đồ. Gọi API qua client sinh từ OpenAPI.
- Lệnh điều khiển: có xác nhận + hiển thị phạm vi tác động + phản hồi trạng thái thực thi.
- Mobile (rules/60) nếu tính năng nằm trong phạm vi Mục XII.

## Bước 5 — i18n & a11y (rules/90)
- Thêm đầy đủ cặp khóa **vi + EN**. Không hard-code chuỗi. Đạt WCAG AA cho thành phần mới.

## Bước 6 — Test (rules/80)
- Unit + integration (Testcontainers). E2E nếu là luồng then chốt.
- F-Txx: bắt buộc test xung đột pha + fail-safe.

## Bước 7 — Hoàn tất Definition of Done
- `mvn verify` / `npm test` xanh; lint/type xanh.
- Cập nhật cột Trạng thái `F-xxx` → `DONE` trong feature-matrix.
- Đảm bảo có metric/log/trace quan sát được.
- PR mô tả gắn mã `F-xxx`.
