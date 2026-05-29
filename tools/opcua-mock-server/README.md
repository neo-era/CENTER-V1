# opcua-mock-server

OPC-UA Server **mô phỏng 1 tủ điều khiển chiếu sáng** (`CB-DEMO-001`) theo đúng [hợp đồng tag](../../docs/architecture/opc-ua-tag-contract.md). Dùng để phát triển/kiểm thử `opcua-gateway`, `ingestion` và làm công cụ sinh tải cho nghiệm thu Mục VIII — **không cần phần cứng thật**.

> ⚙️ Đây là **công cụ dev** (rules/20). Không nằm trong reactor `services/`; build riêng.

## Chức năng

- Tạo node cho **toàn bộ tag** trong hợp đồng (nhóm 1: thời gian/lịch/trạng thái; nhóm 2: đo lường điện; tag hệ thống).
- **Mô phỏng telemetry** biến thiên theo thời gian (chu kỳ 1s): điện áp 3 pha, dòng, P/S/Q, cosφ, tần số ~50Hz, kWh tích lũy; LUX theo chu kỳ ngày/đêm + photocell.
- **Phản ánh lệnh ghi (R/W → PV)**:
  - `1_33_MAN_CTR=true` → bật `1_30_CONTACTOR_C1/C2` (điều khiển tay).
  - `1_07_SETTIME=1` → nạp setpoint thời gian vào PV (`1_22..1_28`) rồi tự reset.
  - `1_20_SETTIME_LAMP=1`, `SETTIME=true` → ghi nhận và reset.
- **Endpoint**: `opc.tcp://localhost:12686/center-v1`
  - `None` — **chỉ dev/test**.
  - `Basic256Sha256` + `SignAndEncrypt` — đúng yêu cầu bảo mật (Mục III.a).
- **Định danh**: Anonymous (test) hoặc Username/Password `center` / `center_dev_pw`.

## Build & chạy

Cần JDK 21 + Maven. Nếu máy chưa có, dùng Docker (xem mục dưới).

```bash
# Build + chạy test
cd tools/opcua-mock-server
mvn -q verify

# Chạy server (Ctrl+C để dừng)
mvn -q exec:java
# hoặc chạy fat-jar sau khi build:
java -jar target/opcua-mock-server.jar
```

### Build bằng Docker (không cần cài JDK/Maven)

```bash
# Từ thư mục gốc repo
docker run --rm -v "${PWD}:/ws" -w /ws/tools/opcua-mock-server maven:3.9-eclipse-temurin-21 mvn -q verify
```
PowerShell: thay `${PWD}` bằng `${PWD}` vẫn đúng, hoặc `$(Get-Location)`.

## Kết nối thử

Trỏ một OPC-UA client (UaExpert, hoặc `opcua-gateway` ở Phase 1) tới
`opc.tcp://localhost:12686/center-v1`, browse node `CB-DEMO-001`, đọc ví dụ:
- `CB-DEMO-001/2_28_F` (tần số), `CB-DEMO-001/2_21_TOTAL_P` (tổng công suất), `CB-DEMO-001/1_293_LUX_VALUE` (lux).

## Lưu ý bảo mật

Chứng chỉ X.509 ở đây là **self-signed cho dev** (sinh tự động vào thư mục tạm). Endpoint `None` **không được dùng** ở môi trường thật. Production: chứng chỉ do CA/đơn vị cấp, quản lý tập trung; bật đúng SignAndEncrypt + X.509 + RBAC (rules/20, rules/70).

## Mở rộng (kế hoạch)

Mô phỏng **N tủ** đồng thời (đổi `CABINET_CODE` thành nhiều tủ) để kiểm thử hiệu năng ≥ 5.000 tủ — xem [capacity-planning.md §9](../../docs/architecture/capacity-planning.md).
