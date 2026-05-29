# mobile — Ứng dụng di động native

Tuân [.claude/rules/60-mobile.md](../.claude/rules/60-mobile.md). Hai app riêng, chung tài khoản (Keycloak OIDC) với web.

- `android/` — Kotlin + Jetpack Compose, minSdk = Android 9.0 (API 28).
- `ios/` — Swift + SwiftUI, tối thiểu iOS 14.

## Tính năng tối thiểu (Mục XII)
Giám sát realtime · push notification (FCM/APNs) · lệnh điều khiển cơ bản theo phân quyền ·
quét QR/Barcode · chụp ảnh hiện trường · GPS · **offline mode** + đồng bộ tự động ·
đăng nhập sinh trắc học (Face ID/Touch ID/vân tay) · token lưu Keystore/Keychain · TLS + cert pinning.

Trạng thái: skeleton — khởi tạo trong Phase 7 ([docs/PLAN.md](../docs/PLAN.md)).
