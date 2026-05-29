# tools — Công cụ hỗ trợ phát triển

## opcua-mock-server (kế hoạch)
OPC-UA Server mô phỏng (Eclipse Milo) triển khai đúng [hợp đồng tag](../docs/architecture/opc-ua-tag-contract.md) — cho phép dev/test `opcua-gateway`, `ingestion` mà không cần phần cứng tủ thật. Sinh telemetry giả lập (điện áp/dòng/công suất/LUX), nhận lệnh ghi (set time, manual control) và phản hồi PV.

## Script khác (theo nhu cầu)
- Seed dữ liệu mẫu (tủ/đèn/khu vực hành chính) cho dev.
- Sinh tải mô phỏng nhiều tủ để kiểm thử hiệu năng (≥ 5000 tủ — Mục VIII).

Trạng thái: skeleton — mock server làm trong Phase 1.
