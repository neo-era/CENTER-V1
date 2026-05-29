# Luật: Kiến trúc & ranh giới service

Tham chiếu: [docs/architecture/overview.md](../../docs/architecture/overview.md).

## Bắt buộc
- **4 lớp**: Field → Connectivity → Application → Presentation. Không gọi tắt xuyên lớp (web KHÔNG gọi thẳng opcua-gateway; phải qua api-gateway + service nghiệp vụ).
- **Mỗi service sở hữu dữ liệu của mình** (database-per-service). KHÔNG truy vấn chéo bảng của service khác; trao đổi qua REST đồng bộ hoặc sự kiện Kafka.
- **Sự kiện chuẩn hóa (canonical)**: chỉ dùng các schema event đã định nghĩa (`CabinetTelemetry`, `CabinetStatus`, `CabinetCommand`, `SignalState`, `AlarmEvent`, `AuditEvent`). Đặt trong `services/libs/events`. Đổi schema = đổi có phiên bản (versioned), không phá ngược.
- **opcua-gateway là biên giới chống vendor lock-in**: mọi thứ hãng-cụ-thể nằm trong gateway. Service nghiệp vụ chỉ thấy mô hình canonical, KHÔNG thấy tên tag thô.
- **Stateless service** ở mức có thể; trạng thái chia sẻ để ở Redis/DB để scale ngang (yêu cầu mở rộng ×10, Mục II.2).

## Giao tiếp
- **Đồng bộ** (REST nội bộ): cho truy vấn cần phản hồi ngay (lấy thông tin tủ).
- **Bất đồng bộ** (Kafka): telemetry, lệnh điều khiển, cảnh báo, audit, webhook.
- Lệnh điều khiển đi qua Kafka topic `command.cabinet` → opcua-gateway thực thi → đọc PV xác nhận.

## Không được
- Không tạo "service tổng" ôm nhiều domain. Một service = một bounded context (xem bảng trong overview).
- Không chia sẻ entity JPA giữa các service. Dùng DTO/event.
- Không thêm phụ thuộc đồng bộ vòng (A→B→A).

## Khi thêm service mới
Dùng skill `scaffold-service`. Cập nhật bảng service trong overview.md và sơ đồ.
