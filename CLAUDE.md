# CLAUDE.md — CENTER-V1

Hướng dẫn cho Claude Code khi làm việc trong repo này. Đọc kỹ trước khi sửa code.

## Dự án là gì

**CENTER-V1** = Phần mềm điều khiển trung tâm cho **hệ thống chiếu sáng đô thị & tín hiệu giao thông**.
Đây là hệ thống kiểu SCADA/IoT đô thị thông minh: giám sát – điều khiển – duy tu – báo cáo hàng nghìn tủ điều khiển và đèn LED node-level, kết nối qua **OPC-UA (IEC 62541)** để **không phụ thuộc nhà sản xuất phần cứng**.

Nguồn yêu cầu gốc (bắt buộc bám sát):
- 📄 [docs/requirements/spec-fulltext.md](docs/requirements/spec-fulltext.md) — toàn văn tiêu chuẩn kỹ thuật.
- ✅ [docs/requirements/feature-matrix.md](docs/requirements/feature-matrix.md) — 64 tính năng chiếu sáng + 12 tính năng tín hiệu GT, mã `F-xxx` để truy vết.
- 🏗️ [docs/architecture/overview.md](docs/architecture/overview.md) — kiến trúc 4 lớp + microservices.
- 🔌 [docs/architecture/opc-ua-tag-contract.md](docs/architecture/opc-ua-tag-contract.md) — **hợp đồng tag OPC-UA** (giao tiếp công khai với phần cứng).
- 📈 [docs/architecture/capacity-planning.md](docs/architecture/capacity-planning.md) — tính dung lượng/sharding/throughput theo số tủ, cơ sở nghiệm thu Mục VIII.
- 🗺️ [docs/PLAN.md](docs/PLAN.md) — lộ trình theo phase.
- 🧭 [docs/adr/0001-tech-stack.md](docs/adr/0001-tech-stack.md) — lý do chọn công nghệ.
- ▶️ [prompts.md](prompts.md) — bộ prompt sẵn-sàng-dùng để thực thi dự án theo từng phase.

> **Quan trọng**: đây là hệ thống hạ tầng đô thị có yếu tố **an toàn vận hành** (đặc biệt module tín hiệu giao thông). Một lệnh sai có thể gây xung đột pha đèn ngoài thực tế. Luôn ưu tiên an toàn, audit, và kiểm thử.

## Ngăn xếp công nghệ (tóm tắt — chi tiết ở ADR-0001)

- **Backend**: Java 21, Spring Boot 3.3, Spring Cloud Gateway, **Eclipse Milo** (OPC-UA).
- **Dữ liệu**: PostgreSQL 16 + **PostGIS**, **TimescaleDB** (telemetry), Redis 7, MinIO (S3), Vault (KMS).
- **Bus**: Apache Kafka; MQTT (EMQX) cho thiết bị nhẹ.
- **IAM**: Keycloak (RBAC, OAuth2, OIDC, SAML, LDAP/AD, MFA).
- **Web**: React 18 + TypeScript + Vite + Ant Design + MapLibre GL + ECharts + react-i18next.
- **Mobile**: Native — Android (Kotlin/Jetpack Compose), iOS (Swift/SwiftUI).
- **Hạ tầng**: Docker + Kubernetes + Helm + Nginx/ModSecurity; Prometheus/Grafana/Loki/Tempo; GitHub Actions.

## Cấu trúc monorepo

```
CENTER-V1/
├── CLAUDE.md                  ← bạn đang đọc
├── .claude/
│   ├── rules/                 ← LUẬT bắt buộc tuân theo (đọc trước khi code)
│   └── skills/                ← quy trình tái sử dụng (scaffold service, thêm feature…)
├── docs/                      ← yêu cầu, kiến trúc, kế hoạch, ADR
├── services/                  ← backend microservices (Maven multi-module)
│   ├── pom.xml                ← parent POM (BOM, version chung)
│   ├── libs/                  ← thư viện dùng chung (events, security, common)
│   ├── api-gateway/  opcua-gateway/  ingestion/  device-registry/
│   ├── lighting-control/  traffic-signal/  scheduling/  alerting/
│   ├── maintenance/  reporting/  gis/  audit/  notification/
├── web/                       ← React app
├── mobile/                    ← android/ (Kotlin) + ios/ (Swift)
├── deploy/                    ← docker-compose.dev.yml, helm/, k8s/
└── tools/                     ← script tiện ích, OPC-UA mock server
```

## Lệnh thường dùng

> Skeleton hiện tại chưa có toàn bộ mã. Khi build từng phần, dùng các lệnh sau.

```bash
# Hạ tầng dev (Postgres+Timescale, Redis, Kafka, Keycloak, MinIO, Vault, GeoServer)
docker compose -f deploy/docker/docker-compose.dev.yml up -d

# Backend (từ thư mục services/)
mvn -q clean verify              # build + test toàn bộ module
mvn -q -pl ingestion -am spring-boot:run   # chạy 1 service

# Web (từ thư mục web/)
npm install && npm run dev       # dev server
npm run build && npm run test    # build + test
npm run lint && npm run typecheck

# Mobile
# Android: ./gradlew assembleDebug (trong mobile/android)
# iOS: xcodebuild / mở mobile/ios trong Xcode
```

Windows: shell mặc định là PowerShell. Dùng cú pháp PowerShell (`$env:VAR`, `;` để nối lệnh) khi chạy trên host; lệnh trong container/Compose là bash.

## Quy ước cốt lõi (chi tiết trong .claude/rules/)

1. **Bám yêu cầu**: mọi thay đổi nghiệp vụ phải gắn mã `F-xxx`; cập nhật trạng thái trong feature-matrix khi xong.
2. **Hợp đồng trước**: sửa OpenAPI/đề xuất tag OPC-UA trước khi hiện thực. Không phá vỡ hợp đồng tag công khai.
3. **Bảo mật mặc định bật**: TLS, RBAC, audit log, validate input — không bao giờ để "làm sau". Xem [rules/70-security.md](.claude/rules/70-security.md).
4. **Không hard-code bí mật**: lấy từ Vault/biến môi trường. Không commit secret, không lock vendor.
5. **Song ngữ + a11y**: mọi chuỗi UI qua i18n (vi/EN); tuân WCAG 2.1 AA. Xem [rules/90-i18n-accessibility.md](.claude/rules/90-i18n-accessibility.md).
6. **An toàn vận hành tín hiệu GT**: không bao giờ áp cấu hình pha mà chưa qua kiểm tra xung đột (F-T11).
7. **Test đi kèm code**: xem [rules/80-testing.md](.claude/rules/80-testing.md). Definition of Done ở [docs/PLAN.md](docs/PLAN.md).

## Luật (.claude/rules/) — đọc theo việc đang làm

| File | Khi nào đọc |
|------|-------------|
| [00-architecture.md](.claude/rules/00-architecture.md) | Thêm/sửa service, ranh giới module, sự kiện Kafka |
| [10-backend-java.md](.claude/rules/10-backend-java.md) | Viết code Java/Spring Boot |
| [20-opc-ua.md](.claude/rules/20-opc-ua.md) | Đụng tới opcua-gateway, tag, thiết bị trường |
| [30-api-design.md](.claude/rules/30-api-design.md) | Thiết kế REST/Open API/Webhook |
| [40-database.md](.claude/rules/40-database.md) | Schema, migration, PostGIS, TimescaleDB |
| [50-frontend-web.md](.claude/rules/50-frontend-web.md) | Code web React |
| [60-mobile.md](.claude/rules/60-mobile.md) | Code Android/iOS |
| [70-security.md](.claude/rules/70-security.md) | Luôn (auth, mã hóa, audit, OWASP, pháp lý) |
| [80-testing.md](.claude/rules/80-testing.md) | Viết test, định nghĩa Done |
| [90-i18n-accessibility.md](.claude/rules/90-i18n-accessibility.md) | Bất kỳ chuỗi/giao diện hiển thị |

## Kỹ năng (.claude/skills/)

- **scaffold-service** — tạo microservice Spring Boot mới đúng chuẩn dự án.
- **add-lighting-feature** — quy trình hiện thực 1 tính năng `F-xxx` đầu-cuối (DB→service→API→web→test→i18n).
- **opc-ua-tag** — tra cứu & ánh xạ tag OPC-UA theo hợp đồng, thêm tag mới an toàn.
- **new-adr** — ghi lại một quyết định kiến trúc mới.

## Khi không chắc

- Mâu thuẫn giữa yêu cầu và hiện trạng code → ưu tiên [docs/requirements](docs/requirements) và hỏi lại.
- Lựa chọn công nghệ mới ngoài ADR-0001 → tạo ADR mới (skill `new-adr`), đừng âm thầm đổi stack.
- Toàn bộ tài liệu nội bộ viết **tiếng Việt** (yêu cầu Mục XV); code/identifier/tài liệu API viết tiếng Anh.
