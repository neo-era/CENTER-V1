---
name: new-adr
description: Ghi lại một Quyết định Kiến trúc (Architecture Decision Record) mới cho CENTER-V1. Dùng khi thay đổi công nghệ, ranh giới service, hợp đồng tag/event quan trọng, hoặc bất kỳ quyết định khó đảo ngược nào.
---

# Skill: Tạo ADR mới

Dùng khi sắp đưa ra một quyết định kiến trúc lệch khỏi [ADR-0001](../../../docs/adr/0001-tech-stack.md) hoặc thêm quyết định mới (đổi stack, thêm hạ tầng, đổi ranh giới service, breaking change hợp đồng).

## Quy trình
1. Tạo file `docs/adr/NNNN-tieu-de-ngan.md` (NNNN tăng dần, 4 chữ số).
2. Viết theo mẫu dưới, **tiếng Việt**, súc tích.
3. Liên kết tới ADR liên quan; nếu thay thế ADR cũ → đánh dấu ADR cũ `Superseded by NNNN`.
4. Cập nhật tài liệu bị ảnh hưởng (overview.md, rules, CLAUDE.md) cho nhất quán.

## Mẫu
```markdown
# ADR-NNNN: <Tiêu đề>

- Trạng thái: Proposed | Accepted | Superseded
- Ngày: YYYY-MM-DD
- Bối cảnh: <vấn đề, ràng buộc, yêu cầu liên quan (trích Mục nào của spec)>

## Quyết định
<chốt điều gì>

## Lý do
<vì sao chọn so với phương án khác>

## Phương án đã cân nhắc
<liệt kê + lý do loại>

## Hệ quả
- Tích cực: ...
- Đánh đổi: ...
- Tác động tới: <service/hợp đồng/tài liệu>
```

## Lưu ý
- Quyết định phải bám yêu cầu gốc và các chuẩn bắt buộc (Mục III, XVI). Không âm thầm đổi stack mà không có ADR.
