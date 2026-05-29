# Luật: OPC-UA & thiết bị trường

Tham chiếu bắt buộc: [docs/architecture/opc-ua-tag-contract.md](../../docs/architecture/opc-ua-tag-contract.md).

## Nguyên tắc nền
- Chuẩn **IEC 62541 (OPC-UA)**. Thư viện: **Eclipse Milo**. Đây là biên giới mở chống vendor lock-in — KHÔNG nhúng logic riêng của hãng phần cứng nào vào service nghiệp vụ.
- Hợp đồng tag là **công khai và ổn định**. Không đổi tên/ý nghĩa tag đang dùng. Thêm tag mới = thêm có phiên bản, cập nhật tài liệu hợp đồng trước.

## Bảo mật kết nối (Mục III.a — bắt buộc)
- SecurityPolicy tối thiểu **Basic256Sha256**, MessageSecurityMode **SignAndEncrypt**. Không dùng `None` ở môi trường thật.
- Xác thực **X.509 certificate** + user/password. Quản lý chứng chỉ tập trung; trust list rõ ràng.
- Kết nối field đi qua **VPN/IPsec** (Mục IV.3, V).

## Độ tin cậy
- **Heartbeat 2 chiều** + keep-alive; mất → đặt `NOTCONNECT=1`, phát `AlarmEvent` (F-026).
- **Automatic backfilling**: khi nối lại, đọc buffer/Historical Access để không mất telemetry (RPO ≤ 5′).
- **Redundancy** (Cold/Warm/Hot) theo IEC 62541-4 cho cụm gateway.
- Subscription: cấu hình `publishingInterval`/`samplingInterval` hợp lý theo loại tag (telemetry điện ~1–5s).

## Ghi tag (Write) — an toàn vận hành
- Tag `R/W` quan trọng (`1_07_SETTIME`, `1_20_SETTIME_LAMP`, `1_33_MAN_CTR`, ngưỡng `2_xx`) phải:
  1. Qua RBAC (đúng quyền + khu vực).
  2. Ghi **audit** (actor, thời điểm, IP, lý do) TRƯỚC khi gửi.
  3. Đọc lại **PV** để xác nhận thành công; báo lỗi nếu PV không khớp sau timeout.
- KHÔNG ghi tag `RO`.

## Chuẩn hóa
- Gateway map tag thô → event canonical (`CabinetTelemetry`/`CabinetStatus`/`CabinetCommand`) rồi publish Kafka. Service khác KHÔNG biết tên tag thô.
- Ánh xạ `cabinetCode` ↔ endpoint OPC-UA + NodeId lấy từ `device-registry`, KHÔNG hard-code.

## Mock/dev
- Dùng OPC-UA mock server (Eclipse Milo) trong `tools/opcua-mock-server` triển khai đúng hợp đồng tag để dev/test không cần phần cứng thật (đã build + test xanh).

## Lưu ý kỹ thuật Eclipse Milo 0.6.x trên JDK 21
- Milo 0.6.x dùng `javax.xml.bind` (JAXB 2.x) đã bị loại khỏi JDK 11+. Mọi module dùng Milo (gateway, mock) phải thêm: `javax.xml.bind:jaxb-api:2.3.1` + `org.glassfish.jaxb:jaxb-runtime:2.3.9` (runtime), nếu không sẽ lỗi `NoClassDefFoundError: javax/xml/bind/DatatypeConverter` lúc khởi động.
- Namespace tùy biến phải override `onDataItemsCreated/Modified/Deleted` và `onMonitoringModeChanged`, delegate sang `SubscriptionModel`, nếu không client subscribe sẽ không nhận dữ liệu.
- Cân nhắc nâng lên Milo 1.x (dùng `jakarta`) khi hiện thực gateway thật — tạo ADR nếu đổi.
