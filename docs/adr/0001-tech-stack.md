# ADR-0001: Lựa chọn ngăn xếp công nghệ (Tech Stack)

- **Trạng thái**: Accepted
- **Ngày**: 2026-05-29
- **Bối cảnh**: Tài liệu yêu cầu kỹ thuật cho phép nhiều công nghệ tương đương; cần chốt một ngăn xếp hiện đại, mã nguồn mở, đáp ứng SCADA quy mô lớn (≥ 5000 tủ), tính mở OPC-UA và pháp lý VN.

## Quyết định

| Hạng mục | Lựa chọn | Lý do |
|----------|----------|-------|
| Backend | **Java 21 + Spring Boot 3.3** | Enterprise, ổn định; **Eclipse Milo** là stack OPC-UA mã nguồn mở tốt nhất; phổ biến với đội ngũ VN |
| Reactive I/O | Spring WebFlux (cho gateway/ingestion) | Chịu nhiều kết nối đồng thời, độ trễ thấp |
| OPC-UA | **Eclipse Milo** (IEC 62541) | Client + Server, bảo mật X.509, subscription, HA |
| API Gateway | Spring Cloud Gateway | Routing, rate limit, OIDC, tích hợp Spring |
| IAM | **Keycloak** | RBAC + OAuth2 + OIDC + SAML 2.0 + LDAP/AD + MFA sẵn có (Mục IX.1) |
| RDBMS | **PostgreSQL 16** | Mã nguồn mở, mạnh, hỗ trợ extension |
| Spatial | **PostGIS** | GIS chuẩn, tích hợp WMS/WFS qua GeoServer |
| Time-series | **TimescaleDB** | Extension Postgres ⇒ một ngữ cảnh vận hành; continuous aggregates |
| Cache/Lock | **Redis 7** (Sentinel) | Cache, distributed lock, rate limit, pub/sub |
| Message bus | **Apache Kafka** | Throughput cao, bất đồng bộ, webhook, backfilling |
| MQTT | **EMQX** | Thiết bị trường nhẹ; bổ trợ OPC-UA |
| Object store | **MinIO** (S3 API) | Ảnh duy tu, backup, scheduled export |
| Secrets/KMS | **HashiCorp Vault** | Khóa AES-256 tập trung, không hard-code (Mục IX.2) |
| Web | **React 18 + TypeScript + Vite** | Hiện đại, hệ sinh thái lớn |
| UI kit | **Ant Design 5** | Admin/dashboard, i18n, a11y tốt |
| Bản đồ | **MapLibre GL JS** + deck.gl | Mã nguồn mở, vector tiles, clustering ≥ 5000 điểm; cấu hình nguồn OSM/Google |
| Biểu đồ | **Apache ECharts** | Realtime, dữ liệu lớn, xuất PNG/SVG |
| Realtime web | WebSocket (STOMP) + SSE | Telemetry & cảnh báo đẩy |
| i18n web | react-i18next (vi/EN) | Chuyển ngữ không cần đăng nhập lại |
| Mobile | **Native: Android (Kotlin + Jetpack Compose), iOS (Swift + SwiftUI)** | Tối ưu offline, QR, sinh trắc học, push, GPS |
| Container | **Docker + Kubernetes ≥ 1.28** | Mở rộng ngang, HA |
| Đóng gói K8s | **Helm** | Quản lý release theo môi trường |
| Reverse proxy | **Nginx** + **ModSecurity WAF** | TLS, WAF (Mục IX.4) |
| Observability | **Prometheus + Grafana + Loki + Tempo** (OpenTelemetry) | Đo uptime 99,9%, P95, tracing |
| CI/CD | **GitHub Actions** | SAST/SCA/DAST trong pipeline |
| Bảo mật quét | Semgrep (SAST), Trivy (SCA/image), OWASP ZAP (DAST) | Mục IX.4 |
| HA Postgres | **Patroni** | Replication + auto failover (RTO/RPO) |
| GIS interop | **GeoServer** | WMS/WFS/WMTS chuẩn (Mục XIII.2) |

## Hệ quả

- **Tích cực**: toàn bộ mã nguồn mở (giảm bản quyền — Mục IV.2), một ngôn ngữ backend (Java) dễ quản trị, OPC-UA và IAM dùng thư viện trưởng thành, đáp ứng HA/DR và pháp lý.
- **Đánh đổi**: mobile native gấp đôi công sức so với cross-platform; bù lại tối ưu offline/sinh trắc học/QR (Mục XII). JVM tốn RAM hơn Go/Rust ở lớp ingestion — chấp nhận, bù bằng scale ngang.
- **Phương án thay thế đã cân nhắc**: Node/NestJS (full-stack TS), .NET (OPC Foundation C#), Go cho gateway — đều khả thi và spec cho phép; chọn Java vì cân bằng độ trưởng thành OPC-UA + nhân lực.

## Tuân thủ chuẩn (Mục III, XVI)

OPC-UA/IEC 62541 · TLS 1.3 · AES-256 · SHA-2 · RSA · X.509/RFC 5280 · OAuth2 · OIDC · SAML 2.0 ·
JSON RFC 7159 · GeoJSON RFC 7946 · OpenAPI 3.0 · WCAG 2.1 AA · UTF-8 (TCVN 6909) · OWASP ASVS 4.0 ·
ISO/IEC 27001/25010/12207 · NĐ 85/2016 (cấp độ 3) · NĐ 13/2023.
