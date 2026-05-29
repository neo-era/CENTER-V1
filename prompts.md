# Bộ Prompt thực thi dự án CENTER-V1

Tập hợp các prompt **sẵn-sàng-dùng** để giao việc cho Claude Code (hoặc lập trình viên) xây dựng phần mềm theo đúng kế hoạch. Mỗi prompt đã neo vào tài liệu, luật (`.claude/rules/`) và skill (`.claude/skills/`) của repo.

## Cách dùng

1. Mở Claude Code tại thư mục gốc repo.
2. Copy nguyên một prompt bên dưới, dán vào, chạy. Làm **tuần tự theo phase** ([docs/PLAN.md](docs/PLAN.md)).
3. Luôn yêu cầu Claude **không commit** cho tới khi bạn review (trừ khi bạn cho phép).
4. Sau mỗi tính năng, kiểm tra **Definition of Done** ([rules/80-testing.md](.claude/rules/80-testing.md)) và cập nhật trạng thái trong [feature-matrix.md](docs/requirements/feature-matrix.md).

Quy ước: `{…}` là chỗ bạn thay giá trị trước khi chạy.

---

## 0) Prompt nền tảng (chạy đầu mỗi phiên làm việc)

```text
Đọc CLAUDE.md, docs/PLAN.md và các file .claude/rules/ liên quan trước khi bắt đầu.
Bám sát yêu cầu gốc trong docs/requirements/ và hợp đồng tag trong
docs/architecture/opc-ua-tag-contract.md. Mọi thay đổi nghiệp vụ phải gắn mã F-xxx,
có test, có bản dịch vi/EN, và ghi audit nếu là hành động thay đổi.
Trước khi sửa nhiều file, trình bày ngắn gọn kế hoạch và chờ tôi xác nhận.
Không commit cho tới khi tôi duyệt.
```

---

## PHASE 0 — Hoàn tất nền tảng

### P0.1 — Build xanh service mẫu + CI
```text
Hoàn tất Phase 0 trong docs/PLAN.md: tạo module Maven "libs/events" với các record event
canonical (CabinetTelemetry, CabinetStatus, CabinetCommand, SignalState, AlarmEvent, AuditEvent)
theo services/libs/events/README.md — mỗi event có eventId, occurredAt (UTC), schemaVersion.
Bật module này trong services/pom.xml, đảm bảo `cd services && mvn -q clean verify` xanh.
Tuân rules/00-architecture và rules/10-backend-java. Thêm unit test cho serialize/deserialize.
```

### P0.2 — Kiểm tra hạ tầng dev
```text
Khởi động deploy/docker/docker-compose.dev.yml và kiểm tra từng dịch vụ healthy
(Postgres+Timescale+PostGIS, Redis, Kafka, Keycloak, MinIO, Vault, GeoServer, EMQX,
Prometheus, Grafana). Tạo docs/dev-setup.md ghi lại cách khởi động, cổng, tài khoản dev,
và cách tạo realm + client Keycloak cho dự án. Không đưa secret thật vào tài liệu.
```

---

## PHASE 1 — Lát cắt dọc telemetry (ưu tiên cao nhất)

### P1.1 — OPC-UA mock server
```text
Dùng skill opc-ua-tag và rules/20-opc-ua. Tạo tools/opcua-mock-server (Eclipse Milo Server)
triển khai ĐÚNG hợp đồng tag trong docs/architecture/opc-ua-tag-contract.md cho 1 tủ mẫu:
- Sinh telemetry giả lập nhóm 2 (điện áp/dòng/công suất/cosφ/tần số/kWh) + LUX thay đổi theo thời gian.
- Cho phép ghi các tag R/W (1_07_SETTIME, 1_20_SETTIME_LAMP, 1_33_MAN_CTR) và phản ánh vào PV.
- Bật SecurityPolicy Basic256Sha256 + SignAndEncrypt + chứng chỉ X.509 self-signed cho dev.
Viết README hướng dẫn chạy. Thêm test khởi động server và đọc được vài tag.
```

### P1.2 — opcua-gateway
```text
Dùng skill scaffold-service tạo service "opcua-gateway" (rules/20). Nó:
- Kết nối tới OPC-UA mock server (tools/opcua-mock-server), subscribe các tag RO nhóm 2 + LUX + cờ trạng thái.
- Map tag thô -> event canonical CabinetTelemetry/CabinetStatus, publish lên Kafka (telemetry.cabinet, status.cabinet).
- Heartbeat 2 chiều: mất kết nối -> set NOTCONNECT, phát AlarmEvent.
- Lấy endpoint/NodeId theo cabinetCode (tạm hard-code cấu hình dev, ghi chú TODO sẽ lấy từ device-registry).
Test bằng Testcontainers (Kafka) + mock server. Không ghi tag RO.
```

### P1.3 — ingestion + TimescaleDB
```text
Dùng skill scaffold-service tạo service "ingestion" (rules/40). Nó tiêu thụ telemetry.cabinet,
chuẩn hóa và ghi vào TimescaleDB hypertable. Tạo migration Flyway cho hypertable + continuous
aggregate (trung bình/Min/Max theo phút và theo ngày) phục vụ biểu đồ. Idempotent khi nhận trùng.
Integration test với Testcontainers (Postgres/Timescale + Kafka). Hiện thực F-012.
```

### P1.4 — device-registry tối thiểu + reporting
```text
Tạo "device-registry" (CRUD tủ tối thiểu: cabinetCode, tên, tọa độ, phân cấp hành chính, endpoint OPC-UA)
và "reporting" (API trả dữ liệu biểu đồ dòng/áp/công suất từ continuous aggregate của TimescaleDB).
Cập nhật opcua-gateway để lấy endpoint từ device-registry thay cho hard-code.
Tuân rules/30-api-design (OpenAPI, RFC 7807). Hiện thực F-005, F-006, F-013, F-014.
```

### P1.5 — api-gateway + Keycloak + web slice
```text
Tạo "api-gateway" (Spring Cloud Gateway: routing, TLS dev, xác thực OIDC qua Keycloak, rate limit).
Khởi tạo web/ (React+TS+Vite+AntD theo rules/50, rules/90): màn hình đăng nhập Keycloak,
bản đồ MapLibre hiển thị 1 tủ với trạng thái realtime (WSS), và biểu đồ ECharts dòng/áp realtime.
Mọi chuỗi qua i18n vi/EN. Hiện thực F-001, F-008. Kết thúc Phase 1: demo "1 tủ sống" đầu-cuối.
```

---

## PHASE 2 — Điều khiển & lập lịch

### P2.1 — lighting-control (lệnh điều khiển)
```text
Dùng skill add-lighting-feature. Tạo "lighting-control": lệnh bật/tắt/tiết giảm 5 cấp.
Lệnh đi qua Kafka command.cabinet -> opcua-gateway ghi tag (1_33_MAN_CTR, dimming) -> đọc PV xác nhận.
Bắt buộc: RBAC theo khu vực (rules/70), Idempotency-Key, ghi AuditEvent (actor + lý do) TRƯỚC khi gửi,
timeout + báo lỗi nếu PV không khớp. Web có nút điều khiển kèm xác nhận + hiển thị phạm vi tác động.
Hiện thực F-003, F-004, F-008, F-017, F-018, F-030, F-031.
```

### P2.2 — scheduling (lịch & đồng bộ thời gian)
```text
Tạo "scheduling": cập nhật giờ tắt/mở/tiết giảm (tag 1_08..1_21), đồng bộ thời gian thực
(tag 1_00..1_07, SETTIME), đồng bộ theo khu vực định nghĩa trước. Mọi thao tác ghi audit.
Hiện thực F-009, F-010, F-019, F-020, F-025, F-027. Tuân skill opc-ua-tag cho việc ghi tag an toàn.
```

---

## PHASE 3 — Cảnh báo & thông báo

### P3.1 — alerting
```text
Tạo "alerting": quy tắc ngưỡng (HI/LO điện áp, dòng, dòng rò; mất kết nối NOTCONNECT; mất điện),
state machine cảnh báo (active/ack/resolved), cấu hình ngưỡng đẩy xuống tủ (tag 2_32.., 2_471).
Phát AlarmEvent. Hiện thực F-011, F-023, F-T08. Test các kịch bản vượt ngưỡng và phục hồi.
```

### P3.2 — notification (đa kênh)
```text
Tạo "notification": gửi cảnh báo qua Viber, Zalo OA, Email (SMTPS), SMS gateway, và Push (FCM/APNs).
Định tuyến theo kênh + phân quyền + khu vực. Retry có backoff, ghi log gửi. Cấu hình nhà cung cấp
qua biến môi trường/Vault (không hard-code khóa). Hiện thực F-026.
```

---

## PHASE 4 — Đèn LED, danh mục & GIS đầy đủ

```text
Mở rộng device-registry: đèn node-level, nhóm đèn, danh mục (loại tủ/đèn/nguồn gốc/NSX),
mẫu thiết bị (tủ/đèn/trụ/cần). Tạo "gis" (PostGIS): tủ/đèn/trụ/cần/cáp trên bản đồ, clustering,
nhiều lớp bản đồ nền, xuất WMS/WFS/GeoJSON qua GeoServer, xuất ảnh/PDF.
Tuân rules/40 (SRID 4326, GIST index). Hiện thực F-002, F-007, F-016, F-029, F-032, F-033, F-056..F-064.
```

---

## PHASE 5 — Quản lý duy tu

```text
Tạo "maintenance": quản lý niên hạn (báo cáo thiết bị đến hạn tuần/tháng/quý), lập lịch duy tu,
lịch sử bảo dưỡng cho mọi loại tài sản (tủ/đèn/trụ/cần/cáp). CRUD + lịch sử + đính kèm ảnh (MinIO).
Hiện thực F-034..F-055. Tuân skill add-lighting-feature và rules/40, rules/80.
```

---

## PHASE 6 — Module tín hiệu giao thông (an toàn vận hành — làm cẩn thận)

```text
Tạo "traffic-signal" (Mục VII). Bắt buộc theo rules/70 và rules/80:
- Giám sát trạng thái pha từng hướng realtime (độ trễ <= 2s), hiển thị trên GIS với đủ trạng thái sự cố.
- Điều khiển: cập nhật pha, làn sóng xanh theo nhóm trục đường, điều khiển tay (vàng nhấp nháy/tắt/chuyển pha).
- F-T11 (BẮT BUỘC): kiểm tra logic XUNG ĐỘT PHA trước khi áp cấu hình — CHẶN cấu hình gây đỏ-đỏ/xanh-xanh trên hướng xung đột.
- F-T12 (BẮT BUỘC): fail-safe — mất kết nối thì chạy lịch nạp gần nhất; không có lịch hợp lệ -> vàng nhấp nháy.
- Mọi thao tác điều khiển ghi audit (người + thời điểm + lý do); nhật ký vận hành >= 12 tháng (F-T10).
Viết test riêng cho F-T11 và F-T12. Hiện thực F-T01..F-T12.
```

---

## PHASE 7 — Open API, tích hợp & di động

### P7.1 — Open API & Webhook
```text
Hoàn thiện Open API (Mục XIII, rules/30): OAuth2 scope cho ứng dụng tích hợp, rate limit/quota,
tài liệu OpenAPI 3.0 (Swagger UI thử gọi được), Webhook đăng ký + ký HMAC + retry (topic webhook.outbound).
Import/export hàng loạt Excel/CSV/JSON/XML/GeoJSON có template + kiểm tra hợp lệ; scheduled export tới SFTP/S3/email.
Hiện thực F-028, F-061..F-064 và Mục XIII.
```

### P7.2 — Mobile Android
```text
Khởi tạo mobile/android (Kotlin + Jetpack Compose, theo rules/60). Đăng nhập OIDC (Keycloak) +
sinh trắc học, token ở Keystore, TLS + cert pinning. Tính năng: giám sát realtime, push (FCM),
lệnh điều khiển cơ bản theo phân quyền, quét QR/Barcode, chụp ảnh hiện trường, GPS, offline mode + đồng bộ.
Mọi chuỗi qua resource vi/EN. Thêm test cơ bản và CI build.
```

### P7.3 — Mobile iOS
```text
Khởi tạo mobile/ios (Swift + SwiftUI, theo rules/60) với tính năng tương đương bản Android:
OIDC + Face ID/Touch ID, token ở Keychain, TLS + cert pinning, realtime, push (APNs),
lệnh cơ bản, QR scan, ảnh hiện trường, GPS, offline mode + đồng bộ. i18n vi/EN. Test + CI.
```

---

## PHASE 8 — Phi chức năng, HA/DR, nghiệm thu

### P8.1 — Kiểm thử hiệu năng
```text
Viết kịch bản tải k6/JMeter và công cụ mô phỏng >= 5000 tủ (mở rộng tools/opcua-mock-server).
Đo và báo cáo: P95 web <= 2s, lệnh điều khiển <= 3s, telemetry <= 5s, >= 200 user đồng thời (Mục VIII).
Xuất báo cáo load/stress/endurance vào docs/test-reports/.
```

### P8.2 — HA/DR
```text
Cấu hình HA trên K8s (deploy/helm, deploy/k8s): nhiều replica, Postgres Patroni (replication + auto failover),
Redis Sentinel, OPC-UA redundancy. Viết runbook DR và kịch bản drill đạt RTO <= 30', RPO <= 5' (Mục X).
Cấu hình backup mã hóa AES-256 (full hằng ngày + incremental 1-4h, PITR, off-site).
```

### P8.3 — An toàn thông tin & tuân thủ
```text
Bật đầy đủ SAST (Semgrep), SCA (Trivy), DAST (OWASP ZAP) trong CI; tích hợp WAF (ModSecurity) trước web.
Rà soát OWASP Top 10 (rules/70). Chuẩn bị hồ sơ cấp độ 3 (NĐ 85/2016) và đối chiếu NĐ 13/2023.
Báo cáo lỗ hổng và khắc phục mức High/Critical về 0 trước nghiệm thu (Mục XVII.3).
```

### P8.4 — Tài liệu, đào tạo, bàn giao
```text
Sinh bộ tài liệu Mục XV bằng tiếng Việt: thiết kế tổng thể/chi tiết, thiết kế CSDL, deployment guide,
sổ tay quản trị, sổ tay người dùng theo vai trò, tài liệu Open API (OpenAPI 3.0). Chuẩn bị kịch bản
đào tạo và môi trường sandbox. Kiểm tra danh mục bàn giao mã nguồn + script + ảnh container.
```

---

## Prompt tái sử dụng (template)

### T1 — Hiện thực một tính năng bất kỳ
```text
Hiện thực tính năng {F-xxx} theo skill .claude/skills/add-lighting-feature.
Bám yêu cầu gốc trong docs/requirements/spec-fulltext.md và mô tả trong feature-matrix.md.
Đủ Definition of Done (rules/80): code + unit/integration test + OpenAPI + vi/EN + audit (nếu có thay đổi).
Cập nhật trạng thái {F-xxx} -> DONE trong feature-matrix.md. Trình bày kế hoạch trước khi sửa nhiều file.
```

### T2 — Tạo service mới
```text
Tạo microservice "{tên}" theo skill .claude/skills/scaffold-service.
Trách nhiệm: {mô tả ngắn}. Dữ liệu sở hữu: {db}. Event tiêu thụ/phát: {topic}.
Đảm bảo mvn verify xanh, có actuator, OpenAPI, RBAC OIDC, và 1 unit + 1 integration test.
```

### T3 — Thêm/đổi tag OPC-UA
```text
Theo skill .claude/skills/opc-ua-tag: tôi cần {đọc/ghi/thêm} tag {tên/nhóm}.
Cập nhật docs/architecture/opc-ua-tag-contract.md TRƯỚC, rồi ánh xạ trong opcua-gateway,
rồi cập nhật tools/opcua-mock-server. Không đổi tên/ý nghĩa tag hiện có. Ghi audit cho tag R/W quan trọng.
```

### T4 — Quyết định kiến trúc mới
```text
Tôi đang cân nhắc {quyết định}. Theo skill .claude/skills/new-adr, tạo ADR mới trong docs/adr/,
nêu bối cảnh (trích Mục liên quan của spec), phương án đã cân nhắc, quyết định và hệ quả.
Cập nhật các tài liệu bị ảnh hưởng cho nhất quán. Chưa code cho tới khi tôi duyệt ADR.
```

### T5 — Review trước khi merge
```text
Review thay đổi hiện tại đối chiếu .claude/rules/ (đặc biệt 70-security và 80-testing) và yêu cầu gốc.
Liệt kê: lỗi đúng/sai logic, rủi ro bảo mật (OWASP), thiếu test, thiếu bản dịch, thiếu audit, và đề xuất sửa.
Nếu liên quan tín hiệu giao thông, kiểm tra kỹ logic xung đột pha (F-T11) và fail-safe (F-T12).
```

### T6 — Sửa lỗi (bug)
```text
Lỗi: {mô tả + cách tái hiện + log}. Tìm nguyên nhân gốc, đề xuất cách sửa tối thiểu và an toàn,
thêm test tái hiện lỗi (regression). Tuân rules liên quan. Giải thích vì sao lỗi xảy ra trước khi sửa.
```

---

## Lưu ý quan trọng khi giao việc cho AI

- **Bám yêu cầu gốc**: khi mâu thuẫn, ưu tiên `docs/requirements/` và hỏi lại — đừng tự suy diễn.
- **An toàn vận hành tín hiệu giao thông** là tối thượng: không bao giờ bỏ kiểm tra xung đột pha.
- **Bảo mật mặc định bật**: không chấp nhận prompt kiểu "tắt auth/TLS cho nhanh".
- **Không lock vendor**: mọi thứ hãng-cụ-thể chỉ nằm trong opcua-gateway.
- **Tài liệu tiếng Việt, code tiếng Anh** (Mục XV).
- Làm **từng phase**, demo được sớm, test đi kèm code.
