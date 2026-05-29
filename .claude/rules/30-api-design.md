# Luật: Thiết kế API & Open API

Tham chiếu yêu cầu: Mục XIII.

## REST
- **HTTPS bắt buộc** (TLS 1.3). JSON theo **RFC 7159** mặc định; hỗ trợ XML khi có header `Accept: application/xml`.
- Đặt tên tài nguyên danh từ số nhiều, kebab/lowercase: `/api/v1/cabinets`, `/api/v1/cabinets/{id}/telemetry`.
- **Versioning ở path**: `/api/v1/...`. Không phá vỡ v1 đang dùng; thêm v2 khi breaking.
- Phân trang chuẩn: `?page=&size=&sort=`; trả `totalElements`, `totalPages`. Lọc qua query param rõ ràng.
- Mã lỗi theo **RFC 7807 Problem Details** (`type`, `title`, `status`, `detail`, `instance`).
- Idempotency: POST tạo lệnh điều khiển nhận `Idempotency-Key`.

## Open API (hợp đồng trước)
- Sinh tài liệu **OpenAPI 3.0** tự động (springdoc) — Swagger UI cho phép thử gọi (Mục XIII.1).
- Thiết kế/sửa contract OpenAPI **trước** khi hiện thực. Contract là nguồn sự thật.
- Xác thực **OAuth2 / API Key** qua Keycloak; phân quyền theo **scope** cho từng ứng dụng tích hợp; áp **rate limit/quota**.

## Webhook (Mục XIII.1)
- Cho phép đăng ký webhook nhận sự kiện (cảnh báo, đổi trạng thái) realtime.
- Ký payload (HMAC), retry có backoff, ghi log gửi. Đẩy qua topic `webhook.outbound`.

## GIS & dữ liệu (Mục XIII.2/3)
- Xuất/nhập: Excel, CSV, JSON, XML, **GeoJSON (RFC 7946)**. Có template + kiểm tra hợp lệ trước khi ghi.
- Interop bản đồ: hỗ trợ **WMS/WFS/WMTS** (qua GeoServer) cho tích hợp Sở ngành/IOC.

## Realtime
- WebSocket (STOMP) hoặc SSE cho telemetry/cảnh báo đẩy tới web. Có xác thực token, có heartbeat.

## Bắt buộc cho mọi endpoint
- Validate input, RBAC, rate limit, audit (nếu là hành động thay đổi), không lộ thông tin nội bộ trong lỗi.
