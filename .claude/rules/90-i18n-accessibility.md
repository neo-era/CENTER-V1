# Luật: Đa ngôn ngữ & Tiếp cận (i18n & a11y)

Tham chiếu: Mục XI.1, chuẩn WCAG 2.1 AA, TCVN 6909:2001 (UTF-8).

## Đa ngôn ngữ
- Hỗ trợ song song **tiếng Việt (mặc định) và tiếng Anh**. Chuyển ngữ ngay trên giao diện, **không cần đăng nhập lại** (Mục XI.1).
- **Mọi chuỗi hiển thị** đi qua hệ i18n (web: react-i18next; mobile: resource strings). KHÔNG hard-code text trong component.
- Key i18n đặt theo namespace/feature (`cabinet.status.disconnected`), không đặt theo nội dung tiếng Việt.
- Encoding **UTF-8** xuyên suốt (TCVN 6909). Hỗ trợ dấu tiếng Việt đầy đủ ở DB, API, file xuất.
- Định dạng theo locale: số, ngày/giờ (hiển thị Asia/Ho_Chi_Minh), đơn vị. Thông báo lỗi/cảnh báo cũng phải dịch.
- Bản dịch thiếu = lỗi review. Cung cấp đủ cặp vi/EN khi thêm chuỗi mới.

## Tiếp cận (WCAG 2.1 mức AA)
- Tương phản màu đạt AA; không truyền tải thông tin chỉ bằng màu (trạng thái tủ/đèn phải có nhãn/biểu tượng kèm).
- Hỗ trợ bàn phím đầy đủ (focus rõ ràng, phím tắt, không bẫy focus).
- Nhãn ARIA/label cho control, ảnh có alt, form có liên kết label.
- Hỗ trợ trình đọc màn hình cho bảng dữ liệu & cảnh báo realtime (aria-live cho thông báo sự cố).
- Kích thước chạm đủ lớn trên mobile; responsive ≥ 1024×768 trên web.

## Kiểm tra
- Lint a11y (eslint-plugin-jsx-a11y), kiểm tra contrast, test bàn phím trong E2E quan trọng.
- Rà soát thiếu key dịch tự động trong CI.
