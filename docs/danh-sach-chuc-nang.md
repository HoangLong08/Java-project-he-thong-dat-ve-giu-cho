# Danh sách chức năng: Đề 05, Hệ đặt vé xem phim có giữ chỗ

- **Đề:** 05, mục 7 của `main.pdf`. Độ khó: khó. Chương nặng nhất: 4. Khung: `src/de05_datve/T05KhungDatVe.java`.
- **Nguồn:** đề 05 trong `main.pdf` và slide `chuong-01-slide.pdf` … `chuong-06-slide.pdf`. Cột Kỹ thuật Java chỉ ghi kỹ thuật có trong các slide này.
- Trường hợp biên có đuôi **(thêm)** là do AI đề xuất, đề không nêu. Nhóm tự quyết giữ hay bỏ.

| Mã | Chức năng | Nguồn | Trường hợp biên | Kỹ thuật Java (chương) | Phụ thuộc | Người phụ trách |
|---|---|---|---|---|---|---|
| F01 | Mô hình ghế: enum trạng thái TRỐNG / ĐANG GIỮ / ĐÃ BÁN, sơ đồ ghế của một suất chiếu | Phần lõi | số ghế ngoài phạm vi (thêm) | enum có hành vi (Ch6), mảng (Ch1) | — | |
| F02 | Giữ ghế (`giuGhe`): TRỐNG → ĐANG GIỮ là một phép nguyên tử | Phần lõi | hai người bấm cùng một ghế cùng lúc; giữ ghế đang bị giữ hoặc đã bán (thêm) | khối `synchronized` (Ch4), hoặc `ReentrantLock` (Ch4) | F01 | |
| F03 | Thanh toán (`thanhToan`): ĐANG GIỮ → ĐÃ BÁN là một phép nguyên tử | Phần lõi | thanh toán một ghế chưa ai giữ; thanh toán ghế đã bán (thêm); người khác thanh toán ghế mình đang giữ (thêm) | khối `synchronized` dùng cùng khoá với F02 (Ch4) | F02 | |
| F04 | Giữ chỗ có thời hạn 5 phút: ghi ai giữ, giữ lúc nào | Phải làm | đúng mốc 5 phút; cho phép giả lập đồng hồ để test khỏi phải chờ (thêm) | record (Ch6), `LocalDateTime` (Ch5), `Supplier` làm đồng hồ (Ch2), `HashMap` (Ch3) | F02 | |
| F05 | Tự động nhả ghế hết hạn: ĐANG GIỮ → TRỐNG, không chờ người bấm | Phải làm | thanh toán sau khi hết hạn giữ phải thất bại; nhả đúng lúc người giữ đang thanh toán (thêm) | luồng nền `Thread` + `sleep` quét ghế hết hạn (Ch4), cờ dừng `volatile` (Ch4), `synchronized` (Ch4) | F04 | |
| F06 | Huỷ giữ: người giữ tự nhả ghế ĐANG GIỮ → TRỐNG | Phải làm (trường hợp biên) | huỷ giữ; huỷ ghế người khác giữ (thêm); huỷ ghế đã bán (thêm) | `synchronized` (Ch4) | F04 | |
| F07 | Phép thử nhiều luồng của nhóm: giữ, huỷ, hết hạn, thanh toán đan xen, chạy lặp 5 lần | Phải làm | hai người bấm cùng một ghế; thanh toán sau khi hết hạn khi có nhiều luồng | `ExecutorService`, `Callable`/`Future`, `Thread.join` (Ch4) | F02–F06 | |
| F08 | Xem sơ đồ ghế một suất chiếu và chọn ghế (giao diện dòng lệnh, tách khỏi nghiệp vụ) | Phải làm | suất hết ghế; nhập số ghế sai (thêm) | mảng (Ch1), `BufferedReader` đọc bàn phím, try-with-resources (Ch5, Ch6), ngoại lệ (Ch6) | F01–F06 | |
| F09 | Báo cáo: đã bán bao nhiêu vé, đang giữ bao nhiêu, còn trống bao nhiêu | Phải làm | suất hết ghế: còn trống = 0; ba số cộng lại luôn bằng tổng số ghế, kể cả khi đang có luồng ghi (thêm) | Stream, `Collectors.groupingBy` + `counting` (Ch2), đọc trong `synchronized` để ba số khớp nhau (Ch4) | F01, F05 | |
| E01 | Nhiều suất chiếu | Mở rộng | cùng số ghế ở hai suất là độc lập (thêm) | `ConcurrentHashMap<suất, sơ đồ ghế>` (Ch5) | F01–F09 | |
| E02 | Chọn nhiều ghế liền kề trong một lần | Mở rộng | giữ được một phần thì phải nhả hết; hai người giữ hai dãy chồng nhau, giữ theo thứ tự tăng dần để khỏi khoá chết (thêm) | giành khoá theo thứ tự cố định để tránh khoá chết (Ch4), `ReentrantLock` thử giành có thời hạn (Ch4) | F02, F06 | |
| E03 | Hoàn vé: ĐÃ BÁN → TRỐNG | Mở rộng | trái với Hình 7.2 (ĐÃ BÁN là trạng thái cuối), phải ghi rõ đây là phép chuyển mới (thêm) | enum có hành vi để khai báo phép chuyển hợp lệ (Ch6), `synchronized` (Ch4) | F03 | |
| E04 | Hàng chờ khi hết ghế: ghế nhả ra thì người đầu hàng được giữ | Mở rộng | suất hết ghế thì vào hàng chờ; người đang chờ rời hàng (thêm) | `ArrayDeque` làm hàng chờ (Ch3), hoặc `BlockingQueue` (Ch4) | F05, F06 | |

## Ghi chú
- **Mốc quan trọng nhất:** xong F01–F03 thì khung phải đạt cả sáu phép thử, trong đó có phép thử 200 luồng cùng tranh một ghế, chạy năm lần lần nào cũng đạt. Chưa đạt thì chưa làm các dòng Phải làm.
- **Chấm thêm:** đề gợi ý `AtomicIntegerArray.compareAndSet`, nhưng slide không dạy nên bảng dùng `synchronized` hoặc `ReentrantLock`. Khi bảo vệ phải giải thích được đọc rồi ghi đã thành một phép nguyên tử ở F02, F03, F05, F06 bằng cách nào, khoá đặt trên đối tượng nào, có mấy khoá và vì sao đủ.
- **Thứ tự:** phần lõi → mô hình giữ chỗ → hết hạn, huỷ giữ → phép thử nhiều luồng → giao diện → báo cáo → mở rộng. Đề không lưu tệp nên không có dòng lưu trữ.
- Bảng do AI soạn nháp. Nhóm dò lại từng dòng với đề trước khi sang bước 2.
