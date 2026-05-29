# Luật: Kiểm thử & Definition of Done

Tham chiếu: Mục VIII (hiệu năng), XVII (nghiệm thu), ISO/IEC 25010.

## Mức kiểm thử
- **Unit**: logic nghiệp vụ thuần (JUnit 5 + AssertJ + Mockito). Nhanh, không I/O.
- **Integration**: service + DB/Kafka thật qua **Testcontainers** (Postgres/Timescale, Kafka, Redis). Không mock hạ tầng quan trọng.
- **Contract**: kiểm tra API khớp OpenAPI; kiểm tra event schema; opcua-gateway test với **OPC-UA mock server** triển khai hợp đồng tag.
- **E2E**: luồng then chốt (telemetry→biểu đồ, lệnh→PV xác nhận) — Playwright cho web.
- **Hiệu năng/tải**: **JMeter/k6** xác minh P95 ≤ 2s, ≥ 5000 tủ, ≥ 200 user đồng thời; load/stress/endurance (Mục VIII).
- **Bảo mật**: SAST/SCA/DAST trong CI (xem rules/70).

## Quy tắc
- Code nghiệp vụ mới **phải có test** đi kèm trong cùng PR. Không hạ coverage tổng.
- Test tên rõ nghĩa, độc lập, không phụ thuộc thứ tự, không dùng thời gian thực bừa bãi (inject clock).
- Module tín hiệu GT: bắt buộc test **kiểm tra xung đột pha** (F-T11) và **fail-safe** (F-T12) — đây là an toàn vận hành.
- Lệnh điều khiển: test idempotency + xác nhận PV + ghi audit.

## Definition of Done cho một tính năng `F-xxx`
1. Code đạt rules tương ứng; `mvn verify` / `npm test` xanh.
2. Có unit + integration test phủ luồng chính & lỗi.
3. Hợp đồng cập nhật (OpenAPI / event / tag) nếu thay đổi.
4. Chuỗi UI có bản dịch **vi + EN**; đạt a11y cơ bản (rules/90).
5. Hành động thay đổi có **audit log**.
6. Cập nhật cột Trạng thái trong [feature-matrix.md](../../docs/requirements/feature-matrix.md) → `DONE`.
7. Quan sát được: có metric/log/trace cần thiết.

## CI bắt buộc xanh trước merge
build + test + lint + SAST + SCA. PR mô tả `F-xxx` liên quan.
