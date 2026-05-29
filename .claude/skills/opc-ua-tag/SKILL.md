---
name: opc-ua-tag
description: Tra cứu, ánh xạ và mở rộng tag OPC-UA theo hợp đồng công khai của CENTER-V1 một cách an toàn (không phá vendor-neutral). Dùng khi làm việc với opcua-gateway, đọc/ghi tag tủ, hoặc thêm tag mới.
---

# Skill: Làm việc với tag OPC-UA

Nguồn sự thật: [docs/architecture/opc-ua-tag-contract.md](../../../docs/architecture/opc-ua-tag-contract.md). Tuân [rules/20-opc-ua.md](../../rules/20-opc-ua.md).

## Khi cần đọc telemetry/trạng thái từ tủ
1. Tra tag trong hợp đồng (nhóm 1 = thời gian/lịch/trạng thái; nhóm 2 = đo lường điện).
2. Tag `RO` → subscribe MonitoredItem ở opcua-gateway; map sang event canonical (`CabinetTelemetry`/`CabinetStatus`).
3. Service nghiệp vụ tiêu thụ event canonical — **không** tham chiếu tên tag thô.

## Khi cần ghi lệnh xuống tủ
1. Chỉ ghi tag `R/W`. KHÔNG ghi `RO`.
2. Tag quan trọng (`1_07_SETTIME`, `1_20_SETTIME_LAMP`, `1_33_MAN_CTR`, ngưỡng `2_xx`):
   - Kiểm RBAC + khu vực.
   - Ghi **audit** trước khi gửi (actor, lý do, IP).
   - Sau khi `Write`, đọc lại **PV** xác nhận; timeout → báo lỗi, không báo "thành công" giả.
3. Lệnh đi qua Kafka `command.cabinet` → gateway thực thi (idempotent).

## Khi cần THÊM tag mới (mở rộng hợp đồng)
> Hợp đồng là công khai & ổn định — đây là việc nhạy cảm.
1. KHÔNG đổi tên/ý nghĩa tag hiện có. Chỉ thêm mới.
2. Đặt tên theo quy ước hiện tại (`<group>_<idx>_<TÊN>`), khai kiểu (Word/Float/Boolean/String) + mức truy cập.
3. Cập nhật `opc-ua-tag-contract.md` (bảng tương ứng) **trước** khi code.
4. Thêm ánh xạ trong opcua-gateway → event canonical (versioned nếu đổi schema event).
5. Cập nhật OPC-UA mock server trong `tools/` để test không cần phần cứng.
6. Cân nhắc tác động tương thích ngược với nhà sản xuất đã tích hợp → nếu lớn, tạo `new-adr`.

## Bảo mật & độ tin cậy (luôn)
SignAndEncrypt + X.509; heartbeat 2 chiều; backfilling khi nối lại; redundancy theo IEC 62541-4. Endpoint/NodeId lấy từ `device-registry`, không hard-code.
