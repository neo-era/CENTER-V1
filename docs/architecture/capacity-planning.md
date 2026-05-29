# Tính toán dung lượng & hiệu năng (Capacity Planning)

> Mục đích: cung cấp **công thức ước lượng**, chiến lược **sharding gateway**, **throughput**, **dung lượng lưu trữ** theo số tủ; dùng làm cơ sở thiết kế hạ tầng và **nghiệm thu hiệu năng (Mục VIII)**.
>
> Tài liệu này là *kế hoạch* — con số cuối phải được xác nhận bằng kiểm thử tải (k6/JMeter + mô phỏng OPC-UA Client) trên hạ tầng thật.

Tham chiếu: [overview.md](./overview.md), [opc-ua-tag-contract.md](./opc-ua-tag-contract.md), yêu cầu Mục IV (hạ tầng), Mục VIII (hiệu năng), Mục X (sao lưu).

---

## 1. Chỉ tiêu phải đáp ứng (Mục VIII)

| Chỉ tiêu | Yêu cầu | Sau mở rộng ×10 (Mục II.2) |
|---|---|---|
| Số tủ / 1 cluster | ≥ 5.000 | ≥ 50.000 |
| Người dùng đồng thời | ≥ 200 | ≥ 2.000 |
| P95 phản hồi Web | ≤ 2 s | giữ nguyên |
| Độ trễ telemetry tủ→TT | ≤ 5 s | giữ nguyên |
| Độ trễ lệnh TT→tủ | ≤ 3 s | giữ nguyên |
| Uptime | ≥ 99,9% | giữ nguyên |
| Lưu trữ | sự kiện realtime ≥ 12 tháng; vận hành ≥ 5 năm | giữ nguyên |
| RTO / RPO | ≤ 30′ / ≤ 5′ | giữ nguyên |

---

## 2. Biến & giả định mặc định

| Ký hiệu | Ý nghĩa | Mặc định lập kế hoạch | Ghi chú |
|---|---|---|---|
| `N` | Số tủ điều khiển | biến đầu vào | 5.000 / 50.000 để tính mẫu |
| `T_total` | Tổng tag/tủ (hợp đồng) | **≈ 140** | xem opc-ua-tag-contract.md |
| `A` | Số biến **analog đo điện thay đổi liên tục**/tủ | **30** | V/I/P/S/Q/PF/F/kWh… (nhóm 2) |
| `A_sub` | Số tag subscribe (RO) thực tế/tủ | **≈ 50** | analog + cờ trạng thái/lỗi |
| `τ_tele` | Chu kỳ lấy mẫu telemetry điện | **5 s** | cấu hình 1–30 s; nhỏ hơn ⇒ tải cao hơn |
| `τ_persist` | Chu kỳ **ghi lịch sử** raw | **60 s** | giám sát realtime ở RAM/Redis; lịch sử 1′ |
| `L` | Số đèn LED node-level | tùy dự án | mỗi đèn ~ vài tag trạng thái, tần suất thấp |
| `U` | Người dùng đồng thời | 200 / 2.000 | |
| `B_c` | Byte/giá trị **sau nén** TimescaleDB | **2 B** | nén cột 90–95% trên schema hẹp |
| `B_u` | Byte/giá trị **chưa nén** (biên an toàn) | **8 B** | dùng để tính dung lượng tối đa |
| `RF` | Hệ số nhân bản (Kafka/DB) | 3 | HA |

> **Nguyên tắc thiết kế quan trọng**: telemetry realtime (≤5s) chỉ cần đi qua bộ nhớ/stream để hiển thị; **lịch sử lâu dài lưu ở cadence thưa hơn (1′)** rồi **gộp (continuous aggregate)** cho biểu đồ 30 ngày/5 năm. Không lưu mọi mẫu 5s suốt 5 năm.

---

## 3. Công thức cốt lõi

### 3.1 Tốc độ telemetry realtime (giá trị/giây)
```
R_rt = N × A / τ_tele                      [giá trị/giây]
```

### 3.2 Tốc độ ghi lịch sử (dòng/ngày)
```
Rows_day   = N × A × (86400 / τ_persist)   [dòng/ngày]
Storage_day(nén)   = Rows_day × B_c        [byte/ngày]
Storage_year(nén)  = Storage_day × 365
```
(Thay `B_c` bằng `B_u` để có biên trên chưa nén.)

### 3.3 Thông điệp Kafka (gộp theo tủ mỗi chu kỳ quét)
```
M = N / τ_tele                              [thông điệp/giây]   (1 msg/tủ/chu kỳ, gói nhiều tag)
ByteRate_kafka ≈ M × S_msg × RF             S_msg ≈ 0,5–1,5 KB/msg
P (số partition) = max( C_consumer , ceil(M / m_p) )   m_p ≈ 3.000–5.000 msg/s/partition
```

### 3.4 Số instance gateway (sharding)
```
Gateways = ceil( N / G ) + R_redundancy     G = số tủ/instance (mặc định 2.500)
MonitoredItems_total = N × A_sub
```

### 3.5 Lệnh điều khiển hàng loạt (chống dồn tải)
```
CmdRate_burst = N / W                        W = cửa sổ thời gian áp lệnh (giây)
⇒ phải staggering ở mức ≤ CmdRate_max (rate-limit) để giữ độ trễ lệnh ≤ 3 s
```

### 3.6 Tải người dùng / API
```
Req/s_peak ≈ U × r_peak                       r_peak ≈ 3–5 req/s/user (lúc cao điểm)
App replicas ≥ ceil( Req/s_peak / q_inst )    q_inst ≈ 500–1.000 req/s/instance
```

---

## 4. Số liệu tính mẫu

### 4.1 Telemetry & Kafka

| Đại lượng | N = 5.000 | N = 50.000 |
|---|---|---|
| `R_rt` (val/s, τ=5s, A=30) | **30.000** | **300.000** |
| Thông điệp Kafka `M` (msg/s) | **1.000** | **10.000** |
| Byte rate Kafka (S_msg≈1KB, RF=3) | ~3 MB/s | ~30 MB/s |
| Partition khuyến nghị (key=cabinetId) | **12–24** | **48–96** |

→ Throughput này **rất nhẹ** với Kafka (1 broker xử lý hàng trăm nghìn msg/s). Nút cổ chai không nằm ở bus.

### 4.2 Lưu trữ TimescaleDB (raw 1′ + aggregate)

| Đại lượng | N = 5.000 | N = 50.000 |
|---|---|---|
| Dòng/ngày (A=30, τ_persist=60s) | 216 triệu | 2,16 tỷ |
| Dung lượng raw/ngày (nén `B_c`=2B) | **~0,43 GB** | **~4,3 GB** |
| Raw 12 tháng (nén) | **~158 GB** | **~1,58 TB** |
| Raw 12 tháng (biên chưa nén `B_u`=8B) | ~631 GB | ~6,3 TB |
| Aggregate giờ/ngày 5 năm (gộp) | vài chục GB | vài trăm GB |

**Nhận xét:**
- **5.000 tủ**: raw 12 tháng (~158 GB nén, ~631 GB biên) **vừa thoải mái** trên cấu hình tối thiểu *SSD NVMe ≥ 2 TB RAID 10* (Mục IV.1).
- **50.000 tủ**: raw 12 tháng ~1,58–6,3 TB ⇒ cần **mở rộng dung lượng / phân mảnh theo thời gian (Timescale chunk) + nén + tier lưu trữ**, hoặc rút ngắn cửa sổ raw và dựa vào aggregate cho lịch sử dài.
- Tag **trạng thái/Boolean** lưu **on-change** (chỉ khi đổi) ⇒ dung lượng nhỏ so với analog, không tính vào đây.
- Đèn LED node-level `L`: chủ yếu trạng thái on-change, tần suất thấp ⇒ tải ghi thấp; điểm cần tối ưu là **hiển thị bản đồ** (clustering, viewport query — rules/50), không phải lưu trữ.

### 4.3 Continuous aggregate (cho biểu đồ — F-013/014/015/021/027)
- Biểu đồ realtime: đọc từ raw 1′ (cửa sổ ngắn) — nhanh.
- Biểu đồ 30 ngày / nhiều tháng: đọc **aggregate giờ/ngày** ⇒ truy vấn nhẹ, đáp ứng **P95 ≤ 2s** ngay cả ở 50.000 tủ.

---

## 5. Sharding `opcua-gateway`

### 5.1 Nguyên tắc
- Mỗi tủ = **1 session OPC-UA** + 1 subscription với `A_sub` monitored item.
- Chia tủ cho nhiều instance gateway theo **khu vực hành chính** (quận/huyện) hoặc theo dải `cabinetCode` ⇒ dễ vận hành, dễ định tuyến cảnh báo.
- Mỗi shard có cặp **redundancy** (Hot/Warm theo IEC 62541-4) cho HA.

### 5.2 Số instance (G = 2.500 tủ/instance, mặc định lập kế hoạch)

| | N = 5.000 | N = 50.000 |
|---|---|---|
| Gateway hoạt động `ceil(N/G)` | 2 | 20 |
| + Redundancy | +1 ÷ +2 | +4 ÷ +6 |
| Tổng monitored items `N×A_sub` (A_sub=50) | 250.000 | 2.500.000 |

> `G` là **giá trị khởi điểm** — phải tinh chỉnh bằng load test: đo CPU/RAM/độ trễ subscription của 1 instance khi tăng dần số tủ tới khi vẫn giữ telemetry ≤ 5s, rồi đặt G = ~70% mức bão hòa. Cấu hình tối thiểu máy OPC-UA (Mục IV.1: 8 nhân/16 GB) là **per-instance**, scale bằng số instance.

### 5.3 Ràng buộc cần kiểm khi sharding
- `publishingInterval`/`samplingInterval` hợp lý theo loại tag (analog điện 1–5s; cờ trạng thái có thể chậm hơn).
- Backfilling khi mất kết nối không gây "bão dữ liệu" lúc nối lại (giới hạn tốc độ nạp buffer).
- Định tuyến `cabinetCode → shard` lấy từ `device-registry`, **không hard-code**.

---

## 6. Lệnh điều khiển — tránh dồn tải

Kịch bản nguy hiểm: **bật/tắt toàn thành phố** cùng lúc.

- **Vận hành thường ngày KHÔNG dùng lệnh hàng loạt realtime.** Lịch tắt/mở/tiết giảm được **nạp xuống tủ** (tag `1_08..1_21`, `1_20_SETTIME_LAMP`) và tủ **tự chạy theo lịch tại chỗ** ⇒ trung tâm không phải gửi 50.000 lệnh mỗi tối. Đây là lý do thiết kế lịch trên thiết bị.
- **Lệnh tay hàng loạt** (sự cố/yêu cầu đặc biệt): áp dụng **staggering + rate-limit**.
  - Ví dụ giữ độ trễ ≤ 3s/tủ với `CmdRate_max = 500 lệnh/s` ⇒ áp cho 50.000 tủ mất ~100s, chia theo shard chạy song song ⇒ giảm còn ~vài chục giây.
  - Mỗi lệnh: RBAC + Idempotency-Key + audit + đọc PV xác nhận (rules/20, rules/70).
- Module tín hiệu GT: lệnh đổi pha luôn qua **kiểm tra xung đột pha (F-T11)** trước khi áp — không gửi hàng loạt bỏ qua kiểm tra.

---

## 7. Người dùng đồng thời & lớp Web/API

| Đại lượng | U = 200 | U = 2.000 |
|---|---|---|
| Req/s cao điểm (`r_peak`=5) | ~1.000 | ~10.000 |
| Replica app-service (`q_inst`≈800) | 2–3 + dự phòng | 14–16 + dự phòng |
| Kết nối realtime (WSS/SSE) | ~200 | ~2.000 |

- Realtime đẩy qua WSS có **throttle/debounce** phía server; client **virtualize** danh sách dài và **viewport query** bản đồ ⇒ giữ P95 ≤ 2s.
- Tách **read path** (reporting/gis) khỏi **write path**; cache Redis cho truy vấn nóng (trạng thái tủ theo khu vực).

---

## 8. Khuyến nghị hạ tầng theo quy mô (điểm khởi đầu)

> Cấu hình Mục IV.1 là **tối thiểu/instance**. Mở rộng bằng **số node**, không bằng "máy to hơn".

| Thành phần | N = 5.000 | N = 50.000 |
|---|---|---|
| opcua-gateway | 2 active + 1 dự phòng | 20 active + 4–6 dự phòng |
| ingestion (consumer) | 2–3 | 12–16 |
| Kafka broker | 3 | 3–5 |
| Kafka partition (telemetry) | 12–24 | 48–96 |
| PostgreSQL/PostGIS | 1 primary + 1 replica (Patroni) | 1 primary + 2 replica + read pool |
| TimescaleDB | gộp hoặc 1 node riêng | node riêng + chunk/nén + tier lưu trữ |
| Redis | Sentinel 3 node | Sentinel/Cluster |
| app-service (mỗi loại) | 2–3 | 14–16 |
| Lưu trữ telemetry 12 tháng | ~0,16–0,63 TB | ~1,6–6,3 TB |

---

## 9. Phương pháp & kịch bản nghiệm thu hiệu năng (Mục VIII)

| Chỉ tiêu | Phương pháp | Công cụ | Tiêu chí PASS |
|---|---|---|---|
| Số tủ đồng thời ≥ 5.000 | Mô phỏng `N` tủ OPC-UA (scale `tools/opcua-mock-server`) kết nối thật vào gateway | Mock OPC-UA Client/Server | Giữ kết nối ổn định, không mất dữ liệu |
| Telemetry ≤ 5 s | So timestamp tạo tại tủ-mô-phỏng vs lúc ghi TimescaleDB | Mock + truy vấn DB | P95 độ trễ ≤ 5 s |
| Lệnh điều khiển ≤ 3 s | End-to-end: gửi lệnh → ghi tag → đọc PV xác nhận | k6 + mock | P95 ≤ 3 s |
| P95 Web ≤ 2 s | Tải `U` người dùng truy cập dashboard/bản đồ/biểu đồ | k6 / JMeter | P95 ≤ 2 s |
| ≥ 200 user đồng thời | Tải đồng thời + WSS realtime | k6 / JMeter | Đạt mục tiêu độ trễ dưới tải |
| Uptime ≥ 99,9% | Giám sát dài hạn + chaos/failover | Prometheus/Grafana | ≥ 99,9% trong cửa sổ đo |
| RTO ≤ 30′ / RPO ≤ 5′ | Drill DR: hỏng primary → failover/khôi phục | Patroni + kịch bản DR | Đạt RTO/RPO |
| Endurance | Chạy tải liên tục ≥ 24–72h | k6/JMeter | Không rò bộ nhớ, không suy giảm |

**Quy trình đo gợi ý:**
1. Triển khai cấu hình theo Mục 8 cho mức N cần nghiệm thu.
2. Dùng `tools/opcua-mock-server` sinh `N` tủ ảo (telemetry 5s + nhận lệnh) → đo telemetry/command latency.
3. Dùng k6/JMeter mô phỏng `U` người dùng → đo P95 Web/API.
4. Chạy load → stress (vượt 100% tải) → endurance (≥24h).
5. Drill failover DB/gateway → đo RTO/RPO.
6. Xuất báo cáo vào `docs/test-reports/` (gắn cấu hình, biểu đồ, kết luận PASS/FAIL).

---

## 10. Đòn bẩy mở rộng (khi cần vượt mốc)

1. **Thêm instance gateway** (giảm `G`) — tuyến tính theo số tủ.
2. **Tăng partition Kafka + consumer ingestion**.
3. **Tách & scale TimescaleDB** (node riêng, chunk theo thời gian, nén, đa node nếu cần).
4. **Tăng cadence aggregate / giảm cửa sổ raw** để kiểm soát dung lượng ở quy mô rất lớn.
5. **Tách read replica** cho reporting/gis; cache Redis tích cực hơn.
6. **Nạp lịch xuống tủ** thay cho lệnh realtime hàng loạt — giảm tải lệnh tận gốc.

> Kết luận: kiến trúc **không có trần cứng** ở mức nghiệp vụ — giới hạn thực tế do số node hạ tầng và cadence dữ liệu quyết định. Mốc 5.000 tủ đạt thoải mái trên cấu hình tối thiểu; 50.000 tủ đạt khi scale theo bảng Mục 8. Mọi con số phải được chốt lại bằng kiểm thử ở Mục 9.
