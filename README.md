# Đề 05: Hệ đặt vé xem phim có giữ chỗ

Bài tập lớn học phần AJP201 (Lập trình Java nâng cao). Người dùng xem sơ đồ ghế, chọn ghế, giữ chỗ 5 phút rồi thanh toán; hết 5 phút chưa thanh toán thì ghế tự nhả. Không bao giờ bán một ghế cho hai người.

- Đề bài: mục 7 của `main.pdf`.
- Bảng chức năng: [docs/danh-sach-chuc-nang.md](docs/danh-sach-chuc-nang.md).
- Sơ đồ (phân rã, kiến trúc tầng, lớp, tuần tự, lưu đồ): [docs/so-do/](docs/so-do/).
- Java 21. Thư viện ngoài duy nhất là JUnit 5 (trong `lib/`), chỉ dùng cho test.

## Cây thư mục

```
Project-he-thong-dat-ve-giu-cho/
├── .project, .classpath, .settings/   dự án Eclipse/VS Code: nguồn src/ và test/, thư viện lib/, đầu ra bin/ (test: bin-test/), JavaSE-21
├── .vscode/settings.json       terminal của nút Run/Debug là cmd với chcp 65001 để in đúng tiếng Việt
├── README.md
├── main.pdf
├── docs/
│   ├── danh-sach-chuc-nang.md
│   └── so-do/                  10 sơ đồ .drawio.png, mở sửa được bằng draw.io
├── lib/
│   └── junit-platform-console-standalone-1.14.4.jar   JUnit 5, chỉ dùng cho test
├── src/
│   └── de05_datve/
│       ├── model/              dữ liệu dùng chung, không phụ thuộc tầng nào
│       │   ├── TrangThaiGhe.java   enum TRONG / DANG_GIU / DA_BAN
│       │   ├── PhieuGiu.java       record: ai giữ ghế nào, từ lúc nào
│       │   ├── BaoCao.java         record: đã bán, đang giữ, còn trống
│       │   ├── KetQua.java         sealed interface: kiểu kết quả duy nhất
│       │   ├── ThanhCong.java      record
│       │   ├── ThatBai.java        record, kèm LyDo
│       │   └── LyDo.java           enum lý do thất bại
│       ├── nghiepvu/           không import giao diện, khung hay test
│       │   ├── SoDoGhe.java        giữ, thanh toán, huỷ giữ, nhả ghế hết hạn, báo cáo
│       │   ├── BoNhaGheTuDong.java luồng nền nhả ghế quá hạn
│       │   └── RapChieu.java       mở rộng E01: nhiều suất chiếu
│       └── giaodien/
│           └── GiaoDienDongLenh.java   giao diện dòng lệnh, có hàm main
└── test/                       phép thử JUnit 5, cùng cây package với src/
    └── de05_datve/model/
        └── KetQuaTest.java     test mẫu cho KetQua
```

Đề không lưu tệp nên không có package `luutru` và thư mục `data/`; trạng thái ghế nằm trong bộ nhớ của `SoDoGhe`. Đề không dùng mạng nên không có package `mang`.

**Kiểu kết quả dùng chung:** mọi phép nghiệp vụ (`giuGhe`, `thanhToan`, `huyGiu`) trả về `KetQua`, gồm `ThanhCong` hoặc `ThatBai(LyDo)`. Không dùng `boolean`, ngoại lệ hay `Optional` để báo thất bại nghiệp vụ.

**Tệp khung:** khi nhận `T05KhungDatVe.java` từ dự án `AJP201-BaiTapLon`, chép vào `src/de05_datve/`. Lớp `BaiLamCuaBan` trong khung gọi `SoDoGhe` và đổi `KetQua` sang kiểu khung cần.

## Biên dịch và chạy (từ thư mục gốc dự án)

PowerShell:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
javac -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp bin de05_datve.giaodien.GiaoDienDongLenh
```

cmd:

```bat
chcp 65001
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin de05_datve.giaodien.GiaoDienDongLenh
```

Dòng đầu và hai tham số `-D...encoding=UTF-8` giúp console Windows in đúng tiếng Việt có dấu; thiếu chúng thì "Chưa làm" hiện thành "Ch?a l?m". Trong PowerShell phải giữ ngoặc kép quanh `-D...`, nếu không PowerShell tách sai tham số.

Chương trình hỏi tên, in sơ đồ 20 ghế (`.` trống, `G` đang giữ, `X` đã bán) và menu `1) Giữ ghế  2) Thanh toán  3) Huỷ giữ  0) Thoát`; ghế giữ quá 5 phút được luồng nền tự nhả. Báo cáo (F09) chưa làm.

## Chạy test (từ thư mục gốc dự án)

Biên dịch cả `src/` và `test/` vào `bin/` với JUnit trên classpath, rồi chạy mọi test tìm thấy trong `bin/`.

PowerShell:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
javac -encoding UTF-8 -d bin -cp lib/junit-platform-console-standalone-1.14.4.jar (Get-ChildItem src, test -Recurse -Filter *.java).FullName
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -jar lib/junit-platform-console-standalone-1.14.4.jar execute -cp bin --scan-class-path
```

cmd:

```bat
chcp 65001
dir /s /b src\*.java test\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp lib\junit-platform-console-standalone-1.14.4.jar @sources.txt
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -jar lib\junit-platform-console-standalone-1.14.4.jar execute -cp bin --scan-class-path
```

Kết quả in dạng cây, cuối có dòng `tests successful` / `tests failed`. Lệnh `java` trả mã thoát 0 khi mọi test đạt, 1 khi có test hỏng. Trong VS Code có thể chạy test ở **Testing view** (biểu tượng bình thí nghiệm), vì `.classpath` đã khai báo `test/` và `lib/`.
