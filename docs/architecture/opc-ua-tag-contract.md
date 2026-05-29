# Hợp đồng Tag OPC-UA — Tủ điều khiển chiếu sáng đô thị

> Nguồn: Mục **XIV** của tài liệu yêu cầu kỹ thuật.
> Đây là **giao tiếp công khai (public contract)**: bất kỳ nhà sản xuất phần cứng tủ nào triển khai đúng namespace + tag này trên OPC-UA Server của họ đều kết nối được vào Trung tâm điều khiển **mà không cần sửa mã lõi** (yêu cầu Mục II.2, V).
>
> Chuẩn nền: **IEC 62541 (OPC-UA)**. Bảo mật: X.509 certificate, message signing, encryption, user/password, per-tag access, heartbeat 2 chiều, automatic backfilling (Mục III.a).
>
> Quy ước: `Word` = UInt16, `Float` = IEEE-754 32-bit, `Boolean` = Bit, `String` = UTF-8.
> Mức truy cập: `RO` = chỉ đọc, `R/W` = đọc/ghi.

## 1. Quy ước NodeId

- Namespace URI: `urn:center-v1:lighting-cabinet`
- Mỗi tag là một Variable Node; `BrowseName` = tên tag (ví dụ `1_08_HOUR_C1_ON`).
- Mỗi tủ là một Object Node gốc; các tag là con của node tủ. Định danh tủ do `device-registry` cấp (`cabinetCode`).
- **Subscriptions**: client (opcua-gateway) subscribe các tag `RO` để nhận MonitoredItem theo `sampling/publishing interval`; ghi tag `R/W` qua `Write` service.

## 2. Nhóm 1 — Thời gian, lịch, trạng thái điều khiển

### 2.1 Cài đặt thời gian (Setpoint thời gian thực)
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_00_SEC_SP` | Word | R/W | Cài đặt giá trị giây |
| `1_01_MIN_SP` | Word | R/W | Cài đặt giá trị phút |
| `1_02_HOUR_SP` | Word | R/W | Cài đặt giá trị giờ |
| `1_03_DOW_SP` | Word | R/W | Thứ (0=CN, 1=T2, …, 6=T7) |
| `1_04_DAY_SP` | Word | R/W | Cài đặt ngày |
| `1_05_MONT_SP` | Word | R/W | Cài đặt tháng |
| `1_06_YEAR_SP` | Word | R/W | Cài đặt năm |
| `1_07_SETTIME` | Word | R/W | `=1`: nạp các giá trị thời gian vào bộ điều khiển |

### 2.2 Lịch tắt/mở/tiết giảm đèn (nhánh C1, C2, tiết giảm C3)
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_08_HOUR_C1_ON` / `1_09_MIN_C1_ON` | Word | R/W | Giờ/phút mở đèn nhánh C1 |
| `1_10_HOUR_C1_OFF` / `1_11_MIN_C1_OFF` | Word | R/W | Giờ/phút tắt đèn nhánh C1 |
| `1_12_HOUR_C2_ON` / `1_13_MIN_C2_ON` | Word | R/W | Giờ/phút mở đèn nhánh C2 |
| `1_14_HOUR_C2_OFF` / `1_15_MIN_C2_OFF` | Word | R/W | Giờ/phút tắt đèn nhánh C2 |
| `1_16_HOUR_DIM_ON` / `1_17_MIN_DIM_ON` | Word | R/W | Giờ/phút bật tiết giảm C3 |
| `1_18_HOUR_DIM_OFF` / `1_19_MIN_DIM_OFF` | Word | R/W | Giờ/phút tắt tiết giảm C3 |
| `1_20_SETTIME_LAMP` | Word | R/W | `=1`: nạp thông số thời gian tắt/mở/tiết giảm vào bộ điều khiển |
| `1_21_SETUP` | Word | R/W | `=11`: chuyển sang chế độ cài đặt |

### 2.3 Thời gian thực hiện tại của bộ điều khiển (Process Value)
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_22_SEC_PV` … `1_28_YEAR_PV` | Word | RO | Giây/phút/giờ/thứ/ngày/tháng/năm thực của bộ điều khiển |
| `1_29_ERR_CODE` | Word | RO | Mã lỗi bộ điều khiển |

### 2.4 Cảm biến ánh sáng (LUX) & photocell
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_291_LUX_LEVEL1` | Word | R/W | Ngưỡng lux chuyển chiều→tối |
| `1_292_LUX_LEVEL2` | Word | R/W | Ngưỡng lux chuyển đêm→sáng |
| `1_293_LUX_VALUE` | Word | RO | Giá trị độ lux hiện tại |
| `1_36_PHOTOCELL_1` | Boolean | RO | Photocell 1 (chiều→tối) |
| `1_37_PHOTOCELL_2` | Boolean | RO | Photocell 2 (đêm→sáng) |
| `1_44_PHOTOCEL_SIGNAL` | Boolean | RO | Báo trời tối (ON khi lux < LEVEL1, OFF khi lux > LEVEL2) |

### 2.5 Ngõ ra contactor & điều khiển
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_30_CONTACTOR_C1` | Boolean | RO | Ngõ ra C1 (1=ON) |
| `1_31_CONTACTOR_C2` | Boolean | RO | Ngõ ra C2 (1=ON) |
| `1_32_CONTACTOR_C3` | Boolean | RO | Ngõ ra tiết giảm C3 (1=ON) |
| `1_33_MAN_CTR` | Boolean | R/W | Lệnh bật đèn từ trung tâm (1=ON) |
| `1_35_SW_HAND_STATUS` | Boolean | RO | Trạng thái công tắc tay tại tủ |

### 2.6 Chế độ vận hành
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_39_MODE1` | Boolean | R/W | Chế độ 1: không tách nhánh C1/C2 |
| `1_40_MODE2` | Boolean | R/W | Chế độ 2: điều khiển riêng từng nhánh |
| `1_41_EN_C1` | Boolean | R/W | Cho phép nhánh C1 (chỉ ở chế độ 2) |
| `1_42_EN_C2` | Boolean | R/W | Cho phép nhánh C2 (chỉ ở chế độ 2) |
| `1_45_EN_DO` | Boolean | R/W | Cho phép giám sát cửa tủ |
| `1_46_ST_DO` | Boolean | RO | Trạng thái cửa tủ (0=đóng, 1=mở) |

### 2.7 Lỗi & sự cố tủ
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_34_ERR_VOLT` | Boolean | RO | Lỗi điện áp |
| `1_38_ERR_TIME` | Boolean | R/W | Lỗi thời gian thực bộ điều khiển |
| `1_43_ERR_CONNECT_ZEN` | Boolean | RO | Lỗi mất kết nối bộ điều khiển |
| `1_47_ERR_PW_OFF_PANEL` | Boolean | RO | Lỗi mất điện lưới tủ |
| `1_48_ERR_PW_OFF_CTT` | Boolean | RO | Lỗi mất điện áp đầu ra contactor |

### 2.8 Thông tin truyền thông (SIM / sóng / GPS / pin)
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `1_49_INFO_SIM` | Word | RO | Nhà mạng (1=Viettel, 2=Mobifone, 3=Vinaphone) |
| `1_50_INFO_ST_SIM` | Word | RO | Dịch vụ (2=GPRS, 3=3G, 4=4G, 5=5G) |
| `1_51_INFO_WAVE` | Word | RO | Cường độ sóng (0–31) |
| `1_52_PERCENT_BATTERY` | Word | RO | % pin/ắc-quy (option) |
| `1_53_GPS` | String | RO | Tọa độ GPS của tủ |
| `1_54_SERI_SIM` | String | RO | Số seri SIM |

## 3. Nhóm 2 — Đo lường điện năng (đồng hồ đa năng)

### 3.1 Điện áp
| Tag | Kiểu | Mô tả |
|-----|------|-------|
| `2_00_V1N`/`2_01_V2N`/`2_02_V3N` | Float RO | Điện áp pha 1/2/3 (V) |
| `2_03_VLN` | Float RO | Điện áp pha trung bình (V) |
| `2_04_V12`/`2_05_V23`/`2_06_V31` | Float RO | Điện áp dây (V) |
| `2_07_VLL` | Float RO | Điện áp dây trung bình (V) |

### 3.2 Dòng điện
| Tag | Kiểu | Mô tả |
|-----|------|-------|
| `2_08_I1`/`2_09_I2`/`2_10_I3` | Float RO | Dòng pha 1/2/3 (A) |
| `2_11_I` | Float RO | Dòng pha trung bình (A) |

### 3.3 Công suất
| Tag | Kiểu | Mô tả |
|-----|------|-------|
| `2_12_P1`/`2_13_P2`/`2_14_P3` | Float RO | Công suất tác dụng pha (kW) |
| `2_15_S1`/`2_16_S2`/`2_17_S3` | Float RO | Công suất biểu kiến pha (kVA) |
| `2_18_Q1`/`2_19_Q2`/`2_20_Q3` | Float RO | Công suất phản kháng pha (kVAr) |
| `2_21_TOTAL_P` / `2_22_TOTAL_S` / `2_23_TOTAL_Q` | Float RO | Tổng P (kW) / S (kVA) / Q (kVAr) |

### 3.4 Hệ số công suất, tần số, điện năng
| Tag | Kiểu | Mô tả |
|-----|------|-------|
| `2_24_PF1`/`2_25_PF2`/`2_26_PF3` | Float RO | Cosφ pha 1/2/3 |
| `2_27_PF` | Float RO | Cosφ trung bình |
| `2_28_F` | Float RO | Tần số lưới (Hz) |
| `2_29_KWH`/`2_30_KVAH`/`2_31_KVARH` | Float RO | Điện năng kWh / kVAh / kVArh |

### 3.5 Ngưỡng cảnh báo & hiệu chỉnh (R/W)
| Tag | Kiểu | Mô tả |
|-----|------|-------|
| `2_32_HI_V` / `2_33_LO_V` / `2_34_HYS_V` | Float R/W | Ngưỡng cao/thấp/độ trễ điện áp pha (V) |
| `2_35..2_43` (HI/LO/HYS I1,I2,I3) | Float R/W | Ngưỡng cao/thấp/độ trễ dòng từng pha (A) |
| `2_44..2_46` NO_LOAD_I1/I2/I3 | Float R/W | Ngưỡng không tải từng pha (A) |
| `2_471_HI_I_LEAK` | Word R/W | Ngưỡng cao dòng rò (mA) |
| `2_472_I_LEAK` | Word RO | Dòng rò của tủ (mA) |
| `2_47_ERR_CODE_PM` | Word RO | Mã lỗi thông số điện |
| `2_48..2_55` EN_HI/LO_V, EN_HI/LO_I1..3 | Boolean R/W | Cho phép kiểm tra từng loại lỗi |
| `2_69_SETUP_PM` | Boolean R/W | `=1`: nạp thông số báo lỗi điện |

### 3.6 Cờ lỗi đo lường (mã lỗi 1–17)
| Tag | Mã lỗi | Tag | Mã lỗi |
|-----|--------|-----|--------|
| `2_56_ERR_HI_V1` | 1 | `2_57_ERR_LO_V1` | 2 |
| `2_58_ERR_HI_V2` | 3 | `2_59_ERR_LO_V2` | 4 |
| `2_60_ERR_HI_V3` | 5 | `2_61_ERR_LO_V3` | 6 |
| `2_62_ERR_HI_I1` | 7 | `2_63_ERR_LO_I1` | 8 |
| `2_64_ERR_HI_I2` | 9 | `2_65_ERR_LO_I2` | 10 |
| `2_66_ERR_HI_I3` | 11 | `2_67_ERR_LO_I3` | 12 |
| `2_68_ERR_CONNECT_PM` | 13 | `2_70_ERR_NO_LOAD_I1` | 14 |
| `2_71_ERR_NO_LOAD_I2` | 15 | `2_72_ERR_NO_LOAD_I3` | 16 |
| `2_73_ERR_I_LEAK` | 17 | | |

> Tất cả cờ lỗi trên là `Boolean RO`.

## 4. Tag hệ thống
| Tag | Kiểu | Truy cập | Mô tả |
|-----|------|----------|-------|
| `NOTCONNECT` | Boolean | RO | Tủ mất kết nối (1=mất, 0=có) |
| `SETTIME` | Boolean | R/W | `=1`: đồng bộ thời gian thực tủ với trung tâm |

## 5. Lưu ý triển khai (cho `opcua-gateway`)

1. **Backfilling**: khi mất kết nối, server tủ buffer dữ liệu; khi nối lại phải đọc historical access (HA) hoặc nhận buffer để không mất telemetry (RPO ≤ 5′).
2. **Heartbeat 2 chiều**: dùng `ServerState` + keep-alive; mất heartbeat → set `NOTCONNECT`, phát cảnh báo F-026.
3. **Ghi an toàn**: lệnh ghi quan trọng (`1_07_SETTIME`, `1_20_SETTIME_LAMP`, `1_33_MAN_CTR`) phải log audit (X-AUDIT) kèm user + lý do, và xác nhận lại bằng PV trước khi báo "thành công".
4. **Chuẩn hóa**: gateway map tag thô → mô hình thống nhất (canonical) `CabinetTelemetry` / `CabinetCommand` (xem [overview.md](./overview.md)), publish lên Kafka.
5. **Định danh**: ánh xạ `cabinetCode` ↔ OPC-UA endpoint + node trong `device-registry`; không hard-code endpoint.
