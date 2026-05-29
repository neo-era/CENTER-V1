# Ma trận tính năng (Feature Traceability Matrix)

> Nguồn: *Tiêu chuẩn và yêu cầu kỹ thuật – Phần mềm điều khiển trung tâm*, Mục VI & VII.
> Mỗi tính năng được gắn mã `F-xxx` để truy vết qua thiết kế, mã nguồn, test và nghiệm thu (UAT).
> Cột **Service** chỉ microservice chịu trách nhiệm chính (xem [docs/architecture/overview.md](../architecture/overview.md)).

Trạng thái: `TODO` (chưa làm) · `WIP` (đang làm) · `DONE` (đã có test + UAT).

---

## A. Điều khiển & giám sát tủ chiếu sáng kết nối trung tâm (F-001 → F-028)

| Mã | Tính năng | Service | Trạng thái |
|----|-----------|---------|-----------|
| F-001 | Hiển thị danh sách tủ + trạng thái trong bán kính 3km theo thời gian thực | gis, lighting-control | TODO |
| F-002 | Danh sách tủ theo phân cấp Quận/Huyện → Phường/Xã → Đường | device-registry | TODO |
| F-003 | Thống kê số lượng tủ theo từng trạng thái | lighting-control | TODO |
| F-004 | Liệt kê danh sách tủ theo từng trạng thái | lighting-control | TODO |
| F-005 | Khai báo tủ kết nối trung tâm mới | device-registry | TODO |
| F-006 | Xem, chỉnh sửa thông tin chi tiết tủ | device-registry | TODO |
| F-007 | Tìm kiếm nhanh tủ theo tên/mã/phường/quận/đường | device-registry | TODO |
| F-008 | Xem nhanh trạng thái của tủ | lighting-control | TODO |
| F-009 | Cập nhật giờ tắt/mở/tiết giảm đèn trên từng tủ | scheduling | TODO |
| F-010 | Đồng bộ thời gian thực trên từng tủ | scheduling, opcua-gateway | TODO |
| F-011 | Cập nhật thông số ngưỡng cảnh báo lỗi | alerting, opcua-gateway | TODO |
| F-012 | Theo dõi chỉ số điện (tần số, dòng rò, cosφ…) theo thời gian thực | ingestion, lighting-control | TODO |
| F-013 | Biểu đồ dòng điện theo thời gian thực | reporting (TSDB) | TODO |
| F-014 | Biểu đồ điện áp theo thời gian thực | reporting (TSDB) | TODO |
| F-015 | Biểu đồ tiêu thụ điện trong ngày & 30 ngày gần nhất | reporting (TSDB) | TODO |
| F-016 | Cập nhật nhóm cho đèn | device-registry | TODO |
| F-017 | Theo dõi trạng thái của đèn | lighting-control | TODO |
| F-018 | Cập nhật thông số tiết giảm đèn theo 5 cấp độ | lighting-control, opcua-gateway | TODO |
| F-019 | Đồng bộ thời gian thực cho toàn bộ tủ kết nối | scheduling | TODO |
| F-020 | Đồng bộ giờ tắt/mở/tiết giảm theo khu vực định nghĩa trước | scheduling | TODO |
| F-021 | Theo dõi biểu đồ LUX để tham khảo giờ tắt/mở đèn | reporting (TSDB) | TODO |
| F-022 | Quản lý người dùng theo phân cấp & khu vực quản lý | iam (Keycloak) | TODO |
| F-023 | Nhật ký báo lỗi thiết bị | alerting, audit | TODO |
| F-024 | Nhật ký thao tác người dùng | audit | TODO |
| F-025 | Nhật ký đồng bộ thời gian tắt/mở/tiết giảm | audit, scheduling | TODO |
| F-026 | Cảnh báo mất kết nối / tắt tủ qua Viber, Zalo, Email, SMS | notification | TODO |
| F-027 | Biểu đồ công suất tiêu thụ của tủ theo khoảng thời gian chọn | reporting (TSDB) | TODO |
| F-028 | Thiết lập API mở để giao tiếp với thiết bị khác nhau | api-gateway, opcua-gateway | TODO |

## B. Điều khiển & giám sát đèn LED kết nối trung tâm (F-029 → F-033)

| Mã | Tính năng | Service | Trạng thái |
|----|-----------|---------|-----------|
| F-029 | Khai báo đèn kết nối trung tâm mới | device-registry | TODO |
| F-030 | Thống kê số lượng đèn theo trạng thái | lighting-control | TODO |
| F-031 | Liệt kê danh sách đèn theo trạng thái | lighting-control | TODO |
| F-032 | Hiển thị đèn + trạng thái theo từng tủ trên bản đồ | gis | TODO |
| F-033 | Xem, chỉnh sửa thông tin chi tiết đèn | device-registry | TODO |

## C. Quản lý duy tu thiết bị chiếu sáng công cộng (F-034 → F-060)

| Mã | Tính năng | Service | Trạng thái |
|----|-----------|---------|-----------|
| F-034 | Báo cáo thiết bị đến niên hạn tuần/tháng/quý tới | maintenance | TODO |
| F-035 | Lập lịch duy tu | maintenance | TODO |
| F-036–F-039 | CRUD + lịch sử duy tu **tủ điều khiển** | maintenance | TODO |
| F-040–F-043 | CRUD + lịch sử duy tu **đèn** | maintenance | TODO |
| F-044–F-047 | CRUD + lịch sử duy tu **trụ đèn** | maintenance | TODO |
| F-048–F-051 | CRUD + lịch sử duy tu **cần đèn** | maintenance | TODO |
| F-052–F-055 | CRUD + lịch sử duy tu **cáp** | maintenance | TODO |
| F-056–F-059 | CRUD **mẫu** tủ/đèn/trụ/cần | maintenance, device-registry | TODO |
| F-060 | Quản lý dữ liệu danh mục (loại tủ, loại đèn, nguồn gốc, NSX…) | device-registry | TODO |

## D. Số hóa dữ liệu trên nền bản đồ GIS (F-061 → F-064)

| Mã | Tính năng | Service | Trạng thái |
|----|-----------|---------|-----------|
| F-061 | Thu thập & tích hợp dữ liệu tủ/đèn/trụ/cần/cáp | gis, device-registry | TODO |
| F-062 | Xử lý, phân tích, tổ chức lưu trữ CSDL chuyên dụng (PostGIS) | gis | TODO |
| F-063 | Phân phối, cung cấp cho hệ thống giám sát điều khiển | gis, api-gateway | TODO |
| F-064 | Xuất thông tin Excel/XML/JSON… | reporting | TODO |

---

## E. Module tín hiệu giao thông (Mục VII) — F-T01 → F-T12

| Mã | Tính năng | Service | Trạng thái |
|----|-----------|---------|-----------|
| F-T01 | Hiển thị tủ tín hiệu trên GIS với các trạng thái (bình thường, vàng nhấp nháy, mất kết nối, mất nguồn, lỗi cảm biến, lỗi đèn cháy) | traffic-signal, gis | TODO |
| F-T02 | Theo dõi trạng thái từng pha đèn (đỏ/vàng/xanh/mũi tên) theo hướng, độ trễ ≤ 2s | traffic-signal, ingestion | TODO |
| F-T03 | Theo dõi mật độ phương tiện (vòng từ / camera AI / radar) | traffic-signal | TODO |
| F-T04 | Cập nhật, đồng bộ pha đèn theo tủ/nhóm trục đường (làn sóng xanh) | traffic-signal, scheduling | TODO |
| F-T05 | Lịch hoạt động theo khung giờ/ngày/lễ + kế hoạch sự kiện đặc biệt | scheduling | TODO |
| F-T06 | Điều khiển tay từ trung tâm (vàng nhấp nháy, tắt, chuyển pha thủ công) | traffic-signal | TODO |
| F-T07 | Điều khiển thích ứng (adaptive) theo mật độ phương tiện | traffic-signal | TODO |
| F-T08 | Cảnh báo sự cố: mất kết nối/nguồn, đèn cháy, **xung đột pha**, lỗi bộ đếm | traffic-signal, alerting | TODO |
| F-T09 | Quản lý duy tu tủ/đèn/cột/cảm biến/camera tín hiệu | maintenance | TODO |
| F-T10 | Lưu nhật ký vận hành ≥ 12 tháng phục vụ hậu kiểm tai nạn | audit | TODO |
| F-T11 | Kiểm tra logic xung đột pha TRƯỚC khi áp dụng cấu hình mới (chặn cấu hình gây xung đột) | traffic-signal | TODO |
| F-T12 | Fail-safe: mất kết nối → chạy lịch nạp gần nhất; không có lịch hợp lệ → vàng nhấp nháy | traffic-signal (edge contract) | TODO |

---

## Yêu cầu xuyên suốt (Cross-cutting) — phải thỏa cho mọi tính năng

| Mã | Yêu cầu | Tham chiếu |
|----|---------|-----------|
| X-SEC | RBAC theo chức năng + khu vực + nhóm thiết bị; MFA/2FA cho quản trị; LDAP/AD/SAML/OIDC | Mục IX.1, [rules/70-security.md](../../.claude/rules/70-security.md) |
| X-CRYPTO | TLS 1.3 in-transit; AES-256 at-rest; khóa quản lý qua Vault (không hard-code) | Mục IX.2 |
| X-AUDIT | Audit log bất biến (append-only), ≥ 12 tháng, IP + UA + thời điểm | Mục IX.3 |
| X-OWASP | Chống SQLi/XSS/CSRF/Path Traversal/Command Injection; Rate limit; CAPTCHA; WAF | Mục IX.4 |
| X-PERF | P95 web ≤ 2s; lệnh điều khiển ≤ 3s; telemetry ≤ 5s; ≥ 5000 tủ; ≥ 200 user | Mục VIII |
| X-HA | 99,9% uptime; cluster Active-Active/Standby; replication; failover; RTO ≤ 30′; RPO ≤ 5′ | Mục II.3, VIII, X |
| X-I18N | Song ngữ vi/EN, chuyển ngữ không cần đăng nhập lại; WCAG 2.1 AA | Mục XI.1, [rules/90-i18n-accessibility.md](../../.claude/rules/90-i18n-accessibility.md) |
| X-OPENAPI | REST/JSON (RFC 7159) + XML; OAuth2/API Key + scope + rate limit; OpenAPI 3.0; Webhook | Mục XIII |
| X-GIS | WMS/WFS/WMTS/GeoJSON interop; nhiều lớp bản đồ nền; clustering; xuất ảnh/PDF | Mục XI.2, XIII.2 |
| X-LAW | NĐ 85/2016 cấp độ 3; NĐ 13/2023 (dữ liệu cá nhân); Luật ATTT 86/2015; OWASP ASVS 4.0 | Mục IX.5, XVI |
