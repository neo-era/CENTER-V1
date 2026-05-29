# Luật: Frontend Web

Tham chiếu yêu cầu: Mục XI.

## Stack
- **React 18 + TypeScript (strict) + Vite**. Ant Design 5 cho UI. TanStack Query (server state) + Zustand (UI state).
- Bản đồ: **MapLibre GL JS** (+ deck.gl cho clustering ≥ 5000 điểm). Biểu đồ: **Apache ECharts**.
- i18n: **react-i18next** (vi/EN). Realtime: WebSocket (STOMP) / SSE.

## Quy ước
- TypeScript `strict: true`. KHÔNG dùng `any` (dùng `unknown` + thu hẹp kiểu). Bật ESLint + Prettier; build fail nếu lỗi lint/type.
- Component hàm + hooks. Tách "smart" (data) khỏi "dumb" (trình bày). Đặt component theo feature folder.
- Gọi API qua lớp client sinh từ **OpenAPI** (openapi-typescript) — không gõ URL rải rác, dùng kiểu chia sẻ với backend contract.
- Mọi lệnh điều khiển (bật/tắt/tiết giảm/đổi pha) phải có **xác nhận** và hiển thị rõ phạm vi tác động; phản hồi trạng thái thực thi.

## Hiệu năng (Mục VIII — P95 ≤ 2s)
- Code-split theo route. Lazy load bản đồ/biểu đồ nặng.
- Bản đồ: clustering + viewport query (chỉ tải đối tượng trong khung nhìn), không tải 5000 điểm 1 lần.
- Realtime: throttle/debounce cập nhật; virtualize danh sách dài (react-window).

## Trình bày
- **Responsive** ≥ 1024×768; tương thích Chrome/Edge/Firefox/Safari bản hiện hành.
- Dashboard tùy biến: kéo-thả widget, lưu trạng thái cá nhân.
- Định dạng số/ngày theo locale; giờ hiển thị theo Asia/Ho_Chi_Minh.
- Xuất biểu đồ PNG/SVG; xuất bản đồ ảnh/PDF (Mục XI.2/3).

## Bắt buộc
- Mọi chuỗi hiển thị qua i18n (xem rules/90). Tuân **WCAG 2.1 AA**.
- Không lưu token nhạy cảm trong localStorage không cần thiết; ưu tiên cookie HttpOnly/secure theo cấu hình IAM. Không tự xử lý mật khẩu — qua Keycloak.
