# CENTER-V1

**Phần mềm điều khiển trung tâm** cho hệ thống **chiếu sáng đô thị & tín hiệu giao thông** — nền tảng giám sát, điều khiển, duy tu và báo cáo theo hướng đô thị thông minh / IoT, kết nối đa nhà sản xuất qua **OPC-UA (IEC 62541)**.

> 📌 Bắt đầu từ [CLAUDE.md](CLAUDE.md) (hướng dẫn cho AI & lập trình viên), rồi [docs/PLAN.md](docs/PLAN.md).

## Tài liệu

| Tài liệu | Nội dung |
|----------|----------|
| [docs/requirements/spec-fulltext.md](docs/requirements/spec-fulltext.md) | Toàn văn tiêu chuẩn kỹ thuật (nguồn gốc) |
| [docs/requirements/feature-matrix.md](docs/requirements/feature-matrix.md) | 76 tính năng (F-xxx) + truy vết |
| [docs/architecture/overview.md](docs/architecture/overview.md) | Kiến trúc 4 lớp + microservices |
| [docs/architecture/opc-ua-tag-contract.md](docs/architecture/opc-ua-tag-contract.md) | Hợp đồng tag OPC-UA (giao tiếp phần cứng) |
| [docs/architecture/capacity-planning.md](docs/architecture/capacity-planning.md) | Tính dung lượng, sharding, throughput, nghiệm thu Mục VIII |
| [docs/adr/0001-tech-stack.md](docs/adr/0001-tech-stack.md) | Lý do chọn công nghệ |
| [docs/PLAN.md](docs/PLAN.md) | Lộ trình theo phase |
| [prompts.md](prompts.md) | Bộ prompt sẵn-sàng-dùng để thực thi dự án theo phase |

## Cấu trúc

```
services/   backend microservices (Java 21 / Spring Boot 3, Maven multi-module)
web/        frontend Web (React + TypeScript)
mobile/     Android (Kotlin) + iOS (Swift)
deploy/     docker-compose dev, Helm, K8s
docs/       yêu cầu, kiến trúc, kế hoạch, ADR
tools/      OPC-UA mock server, script tiện ích
.claude/    rules (luật) + skills (quy trình) cho Claude Code
```

## Khởi động nhanh (dev)

```bash
# 1) Hạ tầng nền (Postgres+Timescale+PostGIS, Redis, Kafka, Keycloak, MinIO, Vault, GeoServer, EMQX, Prometheus, Grafana)
docker compose -f deploy/docker/docker-compose.dev.yml up -d

# 2) Backend
cd services && mvn -q clean verify

# 3) Web
cd web && npm install && npm run dev
```

## Ngăn xếp công nghệ

Java 21 · Spring Boot 3 · Eclipse Milo (OPC-UA) · PostgreSQL/PostGIS/TimescaleDB · Redis · Kafka · Keycloak ·
React/TypeScript · MapLibre · ECharts · Kotlin/Swift · Docker/Kubernetes. Chi tiết: [ADR-0001](docs/adr/0001-tech-stack.md).

## Hiện trạng

Giai đoạn **Phase 0 — Nền tảng** (xem [docs/PLAN.md](docs/PLAN.md)). Skeleton monorepo + tài liệu + luật/skill đã sẵn sàng; các service sẽ được hiện thực theo phase.
