# Luật: Cơ sở dữ liệu

## Phân loại lưu trữ
- **PostgreSQL 16**: dữ liệu quan hệ (tủ, đèn, người dùng, duy tu, lịch…). Mỗi service một schema/DB riêng.
- **PostGIS**: dữ liệu không gian (tọa độ tủ/đèn/trụ/cần/cáp, lớp GIS). SRID **4326** (WGS84) làm chuẩn lưu trữ.
- **TimescaleDB**: telemetry chuỗi thời gian (điện áp, dòng, công suất, LUX, trạng thái pha). Dùng **hypertable** + **continuous aggregate** cho biểu đồ ngày/30 ngày/5 năm (F-013/014/015/021/027).
- **Redis**: cache, distributed lock, rate limit, session, trạng thái realtime tạm.
- **MinIO/S3**: ảnh duy tu hiện trường, file backup, scheduled export.

## Migration
- Bắt buộc **Flyway** (hoặc Liquibase) — mọi thay đổi schema qua migration có version, không sửa tay DB.
- Migration **forward-only**, idempotent, review được. Không xóa cột đang dùng (deprecate trước).

## Quy tắc
- Khóa chính UUID (v7 nếu có) hoặc bigserial nhất quán theo service.
- Mọi bảng có `created_at`, `updated_at` (UTC). Bảng nghiệp vụ chính có soft-delete khi cần lịch sử.
- **Audit log bất biến** (service `audit`): append-only, KHÔNG UPDATE/DELETE; cân nhắc bảng chỉ-chèn + trigger chặn sửa (X-AUDIT, lưu ≥ 12 tháng).
- Telemetry: chính sách retention rõ ràng — sự kiện realtime ≥ 12 tháng, dữ liệu vận hành ≥ 5 năm (Mục VIII). Dùng compression + retention policy của TimescaleDB.
- Index theo truy vấn thực tế (tủ theo khu vực, telemetry theo cabinet+time). Spatial index GIST cho PostGIS.

## Bảo mật dữ liệu (X-CRYPTO)
- Mã hóa at-rest **AES-256** (TDE/volume encryption + cột nhạy cảm). Khóa từ Vault.
- Backup mã hóa AES-256; full hằng ngày + incremental 1–4h; PITR; off-site (Mục X).
- HA: replication + **auto failover** (Patroni). Test DR ≥ 6 tháng/lần.

## Hiệu năng
- Không N+1. Dùng truy vấn tập hợp, batch. Phân trang ở DB, không ở app.
- Kết nối qua pool; tránh giữ transaction dài.
