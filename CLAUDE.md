# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

## Dự án

### Đề và tài liệu
- Đề 05 "Hệ đặt vé xem phim có giữ chỗ", mục 7 của `main.pdf`.
- Bảng chức năng: `docs/danh-sach-chuc-nang.md` (F01–F09 bắt buộc, E01–E04 mở rộng). Cột "Kỹ thuật Java" ghi kỹ thuật dùng cho từng dòng.
- Sơ đồ: `docs/so-do/` (phân rã chức năng, kiến trúc tầng, sơ đồ lớp, tuần tự, lưu đồ). Tệp `.drawio.png` mở sửa được bằng draw.io. Code phải khớp sơ đồ lớp `03-so-do-lop.drawio.png`; đổi thiết kế thì vẽ lại sơ đồ.

### Package và các tầng (`src/de05_datve/`)
- `model`: `TrangThaiGhe`, `PhieuGiu`, `BaoCao`, `KetQua`, `ThanhCong`, `ThatBai`, `LyDo`. Không phụ thuộc tầng nào.
- `nghiepvu`: `SoDoGhe` (F01–F04, F06, F09), `BoNhaGheTuDong` (F05), `RapChieu` (E01). `SoDoGhe` dùng một khoá `khoa` cho mọi phép đọc rồi ghi trạng thái ghế.
- `giaodien`: `GiaoDienDongLenh` (F08, có hàm `main`).
- Tầng nghiệp vụ không phụ thuộc giao diện hay mạng: `nghiepvu` không import `giaodien`, tệp khung hay test, nhờ vậy kiểm thử được riêng bằng JUnit.
- Không có tầng lưu trữ tệp và tầng mạng: đề không lưu tệp, không dùng socket; trạng thái ghế nằm trong bộ nhớ của `SoDoGhe`.
- Tệp khung `T05KhungDatVe.java` (khi có) đặt ở `src/de05_datve/`; lớp `BaiLamCuaBan` trong đó chỉ gọi `SoDoGhe`.

### Cách diễn đạt thất bại dùng chung
- `sealed interface KetQua permits ThanhCong, ThatBai`; thất bại là `ThatBai(LyDo)`.
- Mọi phép nghiệp vụ (`giuGhe`, `thanhToan`, `huyGiu`, …) trả `KetQua`. Không dùng `boolean`, ngoại lệ hay `Optional` để báo thất bại nghiệp vụ.
- Không thêm hàm truy vấn có thể thất bại mà không trả `KetQua`. Đọc trạng thái ghế qua `chupSoDo()`.

### Luật code
- Java 21. Không thêm thư viện nào ngoài JUnit 5.
- Chỉ dùng kỹ thuật có trong slide các chương đã học: `chuong-01-slide.pdf` … `chuong-06-slide.pdf` (ở thư mục cha `..\`).
- Đóng tệp, socket, luồng bằng try-with-resources. Không dùng biến toàn cục (trường `static` có thể thay đổi); hằng `static final` thì được.

### Cách làm việc
- Mỗi lần chỉ làm một chức năng trong bảng (một mã F hoặc E).
- Không sửa test cho xanh. Test đỏ thì tìm nguyên nhân trong code.
- Trả lời bằng tiếng Việt.

### Lệnh biên dịch và chạy (từ `README.md`, chạy ở thư mục gốc dự án)

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
