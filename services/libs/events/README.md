# libs/events — Hợp đồng sự kiện canonical

Thư viện dùng chung định nghĩa các **event chuẩn hóa** trao đổi qua Kafka giữa các service. Đây là hợp đồng — đổi schema phải **có phiên bản (versioned)**, không phá ngược (rules/00).

| Event | Topic | Ý nghĩa |
|-------|-------|---------|
| `CabinetTelemetry` | `telemetry.cabinet` | Telemetry điện/LUX/SIM của tủ (map từ tag OPC-UA nhóm 2) |
| `CabinetStatus` | `status.cabinet` | Trạng thái rời rạc: contactor, photocell, cờ lỗi, NOTCONNECT |
| `CabinetCommand` | `command.cabinet` | Lệnh ghi xuống tủ (set time, set lamp schedule, manual, dimming) |
| `SignalState` | `signal.state` | Trạng thái pha đèn tín hiệu theo hướng + timestamp |
| `AlarmEvent` | `alarm.event` | Sự kiện cảnh báo (loại, mức độ, đối tượng, thời điểm) |
| `AuditEvent` | `audit.event` | Bản ghi kiểm toán (actor, action, target, ip, ua, reason, ts) |

Khuyến nghị định dạng: JSON (RFC 7159) hoặc Avro/Protobuf nếu cần schema registry. Mọi event mang `eventId`, `occurredAt` (UTC), `schemaVersion`.
