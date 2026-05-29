# Luật: Ứng dụng di động (Native)

Tham chiếu yêu cầu: Mục XII.

## Nền tảng
- **Android**: Kotlin + Jetpack Compose, minSdk = Android 9.0 (API 28).
- **iOS**: Swift + SwiftUI, tối thiểu iOS 14.
- Hai app dùng **chung tài khoản** với web (Keycloak OIDC). Kiến trúc khuyến nghị: MVVM + repository.

## Tính năng tối thiểu (bắt buộc)
- Giám sát trạng thái tủ/đèn realtime; nhận **push notification** khi có sự cố (FCM cho Android, APNs cho iOS).
- Lệnh điều khiển cơ bản (bật/tắt/tiết giảm) **theo phân quyền** — có xác nhận, có audit phía server.
- Dành cho duy tu hiện trường: **quét QR/Barcode** tra cứu tủ/đèn; **chụp ảnh** gắn hồ sơ duy tu; ghi **tọa độ GPS**.
- **Offline mode**: cho phép nhập liệu khi mất sóng, lưu cục bộ (Room/CoreData) và **đồng bộ tự động** khi có mạng lại; xử lý xung đột rõ ràng.

## Bảo mật
- Đăng nhập đầu tiên qua OIDC; sau đó cho phép **sinh trắc học** (Face ID/Touch ID/vân tay) mở khóa phiên.
- Token lưu ở **Android Keystore / iOS Keychain**. Không lưu plaintext.
- Bắt buộc HTTPS/TLS 1.3; certificate pinning cho endpoint trung tâm.
- Không log PII/secret; tuân chính sách dữ liệu cá nhân (NĐ 13/2023).

## Chất lượng
- Lint (ktlint/SwiftLint), test (JUnit/Espresso, XCTest). CI build cả hai nền tảng.
- Mọi chuỗi qua resource i18n (vi/EN), không hard-code text.
