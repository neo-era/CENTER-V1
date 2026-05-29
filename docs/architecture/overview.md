# Kiến trúc tổng thể — CENTER-V1

> Phần mềm điều khiển trung tâm cho hệ thống chiếu sáng đô thị & tín hiệu giao thông.
> Tuân theo mô hình **4 lớp** (Mục II.1) và kiến trúc **microservices** (SOA), tính mở dựa trên **OPC-UA (IEC 62541)**.

## 1. Sơ đồ 4 lớp

```
┌───────────────────────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER                                                         │
│  • Web (React + TS, responsive, vi/EN, WCAG 2.1 AA, MapLibre, ECharts)      │
│  • Mobile native: Android (Kotlin/Compose) + iOS (Swift/SwiftUI)            │
│  • Dashboard điều hành (tùy biến widget) · Cổng tài liệu Open API (Swagger) │
└───────────────▲─────────────────────────────────────────▲──────────────────┘
                │ HTTPS/WSS (TLS 1.3)                       │ REST/OIDC
┌───────────────┴─────────────────────────────────────────┴──────────────────┐
│  API GATEWAY (Spring Cloud Gateway)  +  IAM (Keycloak: RBAC/OAuth2/OIDC/MFA) │
├──────────────────────────────────────────────────────────────────────────── ┤
│  APPLICATION LAYER (microservices Java 21 / Spring Boot 3)                   │
│  device-registry · lighting-control · traffic-signal · scheduling ·         │
│  alerting · maintenance · reporting · gis · audit · notification            │
│        ▲                        ▲                         ▲                  │
│        │  Kafka (event bus)     │  Redis (cache/lock)     │  REST sync       │
├────────┼────────────────────────┼─────────────────────────┼──────────────── ┤
│  DATA: PostgreSQL16+PostGIS (relational+spatial) · TimescaleDB (telemetry)  │
│        Redis 7 · MinIO/S3 (ảnh, backup, export) · Vault (secrets/KMS)       │
├──────────────────────────────────────────────────────────────────────────── ┤
│  CONNECTIVITY LAYER                                                          │
│  ingestion ◄─ Kafka ─► opcua-gateway (Eclipse Milo) · MQTT broker (EMQX)    │
│                         REST/HTTPS device gateway                            │
└───────────────▲──────────────────────────────────────────────────────────── ┘
                │ OPC-UA (X.509, sign+encrypt) qua VPN/IPsec, GPRS/3G/4G/5G
┌───────────────┴───────────────────────────────────────────────────────────── ┐
│  FIELD LAYER: tủ chiếu sáng · tủ tín hiệu GT · đèn LED node · đồng hồ điện ·  │
│  photocell · LUX · GPS · modem · cảm biến mật độ / camera AI / radar          │
└────────────────────────────────────────────────────────────────────────────── ┘
```

## 2. Microservices & trách nhiệm

| Service | Trách nhiệm | DB chính | Tính năng |
|---------|-------------|----------|-----------|
| **api-gateway** | Routing, TLS termination, rate limit, WAF hook, OIDC verify, OpenAPI aggregation | — | X-OPENAPI, X-OWASP |
| **iam** (Keycloak) | RBAC theo chức năng/khu vực/nhóm; OAuth2/OIDC/SAML/LDAP/AD; MFA/2FA | own | F-022, X-SEC |
| **opcua-gateway** | OPC-UA client/server (Eclipse Milo); subscribe RO tag, write R/W tag; heartbeat; backfilling; redundancy | — (stateless) | F-010/011/018/028, hợp đồng tag |
| **ingestion** | Nhận telemetry từ Kafka, chuẩn hóa, ghi TimescaleDB, phát cảnh báo ngưỡng | TimescaleDB | F-012, F-T02 |
| **device-registry** | CRUD tủ/đèn/trụ/cần/cáp + phân cấp hành chính + danh mục + mẫu thiết bị | PostgreSQL | F-002/005/006/007/016/029/033/056-060 |
| **lighting-control** | Trạng thái tủ/đèn realtime, thống kê, lệnh bật/tắt/tiết giảm 5 cấp | PostgreSQL + Redis | F-003/004/008/017/030/031 |
| **traffic-signal** | Giám sát pha, điều khiển, làn sóng xanh, kiểm tra **xung đột pha**, adaptive, fail-safe | PostgreSQL | F-T01..F-T12 |
| **scheduling** | Lịch tắt/mở/tiết giảm, đồng bộ thời gian, lịch theo khu vực/khung giờ/lễ | PostgreSQL | F-009/010/019/020/025, F-T04/T05 |
| **alerting** | Quy tắc ngưỡng, phát hiện sự cố, trạng thái cảnh báo, escalation | PostgreSQL + Redis | F-011/023, F-T08 |
| **notification** | Gửi Viber/Zalo/Email/SMS/Push theo kênh & phân quyền | — | F-026 |
| **maintenance** | Quản lý duy tu, niên hạn, lịch sử bảo dưỡng mọi loại tài sản | PostgreSQL | F-034..F-055, F-T09 |
| **reporting** | Biểu đồ realtime/lịch sử, mẫu báo cáo, xuất PDF/Excel/CSV/JSON/XML | TimescaleDB + PostgreSQL | F-013/014/015/021/027/064 |
| **gis** | Dữ liệu không gian, lớp bản đồ, clustering, WMS/WFS/GeoJSON, GeoServer | PostGIS | F-001/032/061-063, X-GIS |
| **audit** | Audit log bất biến (append-only), tra cứu, xuất, lưu ≥ 12 tháng | PostgreSQL (immutable) | F-024/025, F-T10, X-AUDIT |

## 3. Mô hình dữ liệu chuẩn hóa (canonical events trên Kafka)

- `CabinetTelemetry` — telemetry tủ chiếu sáng (map từ hợp đồng tag OPC-UA nhóm 2 + LUX + SIM).
- `CabinetStatus` — trạng thái rời rạc (contactor, photocell, cờ lỗi, NOTCONNECT).
- `CabinetCommand` — lệnh ghi (set time, set lamp schedule, manual control, dimming level).
- `SignalState` — trạng thái pha đèn tín hiệu theo hướng + timestamp.
- `AlarmEvent` — sự kiện cảnh báo (loại, mức độ, đối tượng, thời điểm).
- `AuditEvent` — bản ghi kiểm toán (actor, action, target, ip, ua, reason, ts).

Topic Kafka: `telemetry.cabinet`, `status.cabinet`, `command.cabinet`, `signal.state`, `alarm.event`, `audit.event`, `webhook.outbound`.

## 4. Quyết định kiến trúc then chốt

1. **OPC-UA là biên giới chống vendor lock-in** — mọi nhà sản xuất tuân hợp đồng tag ([opc-ua-tag-contract.md](./opc-ua-tag-contract.md)) là kết nối được. Mã lõi không phụ thuộc hãng.
2. **TimescaleDB thay vì InfluxDB** — là extension của PostgreSQL ⇒ một ngữ cảnh vận hành/sao lưu, SQL chuẩn, continuous aggregates cho biểu đồ 30 ngày/5 năm. (InfluxDB là phương án tương đương được spec cho phép.)
3. **Keycloak cho IAM** — đáp ứng trọn gói RBAC + OAuth2 + OIDC + SAML 2.0 + liên kết LDAP/AD + MFA (Mục IX.1) thay vì tự xây.
4. **Kafka làm xương sống bất đồng bộ** — chịu tải ≥ 5000 tủ, tách ingestion khỏi nghiệp vụ, hỗ trợ webhook & backfilling.
5. **HA & DR**: cluster K8s đa node, Postgres Patroni (replication + auto failover), Redis Sentinel, OPC-UA redundancy (IEC 62541-4). RTO ≤ 30′, RPO ≤ 5′.

> 📈 Tính toán dung lượng, sharding gateway, throughput và phương pháp nghiệm thu hiệu năng theo số tủ: xem [capacity-planning.md](./capacity-planning.md).

## 5. Bản đồ luồng dữ liệu (telemetry → biểu đồ)

```
Tủ (OPC-UA Server) ──subscribe──► opcua-gateway ──normalize──► Kafka(telemetry.cabinet)
   └► ingestion ──► TimescaleDB ──continuous aggregate──► reporting ──► Web (ECharts)
   └► alerting (so ngưỡng) ──AlarmEvent──► notification (Viber/Zalo/Email/SMS) + Web (WSS)
```

## 6. Luồng lệnh điều khiển (≤ 3s, có audit)

```
Web/Mobile ─► api-gateway ─(OIDC, RBAC)─► lighting-control / traffic-signal
   └► (traffic-signal: kiểm tra xung đột pha F-T11 TRƯỚC khi áp dụng)
   └► Kafka(command.cabinet) ─► opcua-gateway ─Write─► Tủ ─► đọc PV xác nhận
   └► audit.event (actor + reason) ─► audit
```
