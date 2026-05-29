# Kế hoạch triển khai — CENTER-V1

> Lộ trình xây dựng phần mềm điều khiển trung tâm theo giai đoạn, ưu tiên một **lát cắt dọc chạy được** sớm rồi mở rộng theo chiều ngang. Mỗi tính năng truy vết theo mã `F-xxx` trong [feature-matrix.md](./requirements/feature-matrix.md).

## Nguyên tắc

1. **Vertical slice trước**: dựng đường đi telemetry & lệnh xuyên suốt (tủ mô phỏng → gateway → DB → API → web) trước khi làm rộng tính năng.
2. **Mọi giai đoạn đều "production-shaped"**: có test, có observability, có bảo mật từ đầu (security & i18n không để cuối).
3. **Hợp đồng trước, hiện thực sau**: OpenAPI 3.0 và hợp đồng tag OPC-UA là nguồn sự thật.
4. **Definition of Done** mỗi tính năng: code + unit/integration test + bản dịch vi/EN + audit log (nếu là hành động) + cập nhật trạng thái trong feature-matrix + mục OpenAPI.

---

## Phase 0 — Nền tảng (Foundation) ✅ đang thực hiện

- [x] Phân tích yêu cầu, ma trận tính năng, hợp đồng tag OPC-UA
- [x] ADR tech stack, kiến trúc 4 lớp
- [x] CLAUDE.md, .claude/rules, .claude/skills
- [x] Skeleton monorepo + `docker-compose.dev.yml` hạ tầng nền (Postgres/Timescale, Redis, Kafka, Keycloak, MinIO, Vault, GeoServer)
- [ ] Parent `pom.xml` + 1 service mẫu build xanh + CI khung

## Phase 1 — Lát cắt dọc telemetry (Vertical slice)

**Mục tiêu**: thấy 1 tủ "sống" trên web theo thời gian thực.

- [x] **`tools/opcua-mock-server`** — OPC-UA Server (Eclipse Milo) mô phỏng tủ `CB-DEMO-001` theo hợp đồng tag; build + integration test **xanh** (đọc telemetry, ghi `1_33_MAN_CTR` phản ánh `1_30_CONTACTOR_C1`). Xem [tools/opcua-mock-server](../tools/opcua-mock-server/).
- `opcua-gateway`: kết nối OPC-UA Server mô phỏng trên, subscribe nhóm 2 (điện) + LUX, publish `telemetry.cabinet`.
- `ingestion`: tiêu thụ Kafka → ghi TimescaleDB → hypertable.
- `device-registry`: CRUD tủ tối thiểu + ánh xạ `cabinetCode` ↔ OPC-UA endpoint.
- `reporting`: API biểu đồ dòng/áp/công suất (continuous aggregate).
- `web`: bản đồ MapLibre + 1 tủ + biểu đồ ECharts realtime qua WSS.
- Hạ tầng: api-gateway + Keycloak login, observability cơ bản.
- **Tính năng đạt**: F-001, F-008, F-012, F-013, F-014.

## Phase 2 — Điều khiển & lịch (Control & scheduling)

- `lighting-control`: lệnh bật/tắt/tiết giảm 5 cấp (`1_33_MAN_CTR`, dimming), xác nhận qua PV.
- `scheduling`: cập nhật giờ tắt/mở/tiết giảm, đồng bộ thời gian, lịch theo khu vực/khung giờ.
- Audit log mọi lệnh điều khiển (X-AUDIT).
- **Tính năng**: F-003/004/009/010/017/018/019/020, F-027, F-024/025.

## Phase 3 — Cảnh báo & thông báo (Alerting & notification)

- `alerting`: quy tắc ngưỡng (HI/LO V/I, dòng rò, mất kết nối), state machine cảnh báo.
- `notification`: tích hợp Viber, Zalo OA, Email (SMTPS), SMS gateway; push mobile.
- **Tính năng**: F-011, F-023, F-026.

## Phase 4 — Đèn LED, danh mục & GIS đầy đủ

- `device-registry`: đèn node-level, nhóm đèn, danh mục, mẫu thiết bị, phân cấp hành chính.
- `gis`: PostGIS đầy đủ tủ/đèn/trụ/cần/cáp, clustering, lớp bản đồ, WMS/WFS qua GeoServer, xuất ảnh/PDF.
- **Tính năng**: F-002/005/006/007/016/029-033/056-064.

## Phase 5 — Quản lý duy tu (Maintenance)

- `maintenance`: niên hạn, lập lịch duy tu, lịch sử bảo dưỡng cho mọi loại tài sản.
- **Tính năng**: F-034..F-055.

## Phase 6 — Module tín hiệu giao thông

- `traffic-signal`: giám sát pha (≤2s), điều khiển tay, làn sóng xanh, **kiểm tra xung đột pha (F-T11)**, adaptive, fail-safe (F-T12), nhật ký ≥12 tháng.
- **Tính năng**: F-T01..F-T12.

## Phase 7 — Open API, tích hợp & di động

- Open API hoàn chỉnh (OAuth2 scope, rate limit, Webhook, OpenAPI 3.0 docs).
- Tích hợp IOC/GIS Sở ngành (WMS/WFS/WMTS/GeoJSON), import/export hàng loạt, scheduled export.
- Mobile native Android + iOS: realtime, push, lệnh cơ bản, QR/Barcode, ảnh hiện trường, GPS, **offline mode**.
- **Tính năng**: F-028, F-061-064, Mục XII, XIII.

## Phase 8 — Phi chức năng, HA/DR, nghiệm thu

- Tải & bền: JMeter/k6 đạt P95 ≤2s, ≥5000 tủ, ≥200 user (Mục VIII).
- HA: cluster K8s, Patroni failover, Redis Sentinel, OPC-UA redundancy. DR drill (RTO≤30′, RPO≤5′).
- An toàn thông tin: SAST/SCA/DAST, WAF, pen-test, hồ sơ cấp độ 3 (NĐ 85/2016).
- Song ngữ vi/EN đầy đủ, WCAG 2.1 AA audit.
- Tài liệu (Mục XV), đào tạo, bàn giao mã nguồn.

---

## Bảng phụ thuộc (rút gọn)

```
Phase 0 ─► Phase 1 ─┬─► Phase 2 ─► Phase 3
                    ├─► Phase 4 ─► Phase 5
                    └─► Phase 6
Phase 2,3,4,5,6 ─► Phase 7 ─► Phase 8 (nghiệm thu)
```

## Theo dõi tiến độ

Cập nhật cột **Trạng thái** trong [feature-matrix.md](./requirements/feature-matrix.md) khi mỗi `F-xxx` đạt Definition of Done. Đây là nguồn truy vết cho nghiệm thu chức năng (Mục XVII.1).
