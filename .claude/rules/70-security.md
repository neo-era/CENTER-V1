# Luật: An toàn thông tin (LUÔN áp dụng)

Tham chiếu: Mục IX, X; chuẩn OWASP ASVS 4.0, ISO/IEC 27001. Đây là hệ thống cấp độ 3 (NĐ 85/2016).

## Định danh & phân quyền (X-SEC)
- **RBAC** theo chức năng + khu vực địa lý quản lý + nhóm thiết bị. Phân quyền do **Keycloak** quản lý; service verify token (JWT/OIDC) và áp scope.
- **MFA/2FA bắt buộc** cho tài khoản quản trị (Google/Microsoft Authenticator, OTP SMS/Email).
- Hỗ trợ liên kết **LDAP/AD/SAML 2.0/OIDC** với hệ xác thực của chủ quản.
- Chính sách mật khẩu: ≥ 8 ký tự, đủ hoa/thường/số/đặc biệt; đổi định kỳ; chống tái sử dụng N mật khẩu gần nhất (cấu hình trong Keycloak).
- Nguyên tắc **least privilege**, deny-by-default. Kiểm tra quyền ở phía server, không tin client.

## Mã hóa (X-CRYPTO)
- In-transit: **TLS 1.3** (tối thiểu 1.2), cipher theo khuyến nghị NIST.
- At-rest: **AES-256** cho DB, backup, log nhạy cảm.
- Khóa qua **Vault** (KMS). TUYỆT ĐỐI không hard-code secret/khóa trong mã nguồn hay file cấu hình. Không commit secret.

## Audit log (X-AUDIT)
- Ghi mọi: đăng nhập/xuất, đổi cấu hình, **lệnh điều khiển**, CRUD người dùng — kèm IP, user-agent, thời điểm, lý do.
- Append-only, **không cho sửa/xóa**, lưu ≥ 12 tháng, sao lưu định kỳ. Có công cụ tra cứu/xuất.

## Chống tấn công (X-OWASP — OWASP Top 10)
- Chống **SQLi** (chỉ dùng parameterized query/JPA, không nối chuỗi SQL), **XSS** (escape output, CSP), **CSRF** (token/SameSite), **Path Traversal**, **Command Injection**.
- **Rate limiting + CAPTCHA** tại điểm xác thực (chống brute-force/DDoS lớp ứng dụng).
- **WAF** (ModSecurity) trước lớp web.
- Quét định kỳ: **SAST** (Semgrep), **SCA** (Trivy/Dependabot), **DAST** (OWASP ZAP) — trong CI, ≥ 6 tháng/lần đánh giá đầy đủ.

## An toàn vận hành đặc thù
- Module tín hiệu GT: **không bao giờ** áp cấu hình pha chưa qua kiểm tra xung đột (F-T11). Fail-safe khi mất kết nối (F-T12).
- Lệnh điều khiển: xác nhận lại bằng PV; timeout & cảnh báo nếu không xác nhận được.

## Pháp lý (X-LAW)
- Tuân Luật ATTT 86/2015, NĐ 85/2016 (cấp độ 3), **NĐ 13/2023 (bảo vệ dữ liệu cá nhân)**, Luật Giao dịch điện tử 20/2023.
- Tối thiểu hóa thu thập PII; có cơ sở pháp lý; hỗ trợ quyền của chủ thể dữ liệu.

## Tuyệt đối không
- Không tắt xác thực/TLS "cho tiện dev" trong code dùng chung. Dùng cấu hình môi trường riêng.
- Không log mật khẩu/token/PII thô. Không trả stack trace/thông tin nội bộ ra client.
