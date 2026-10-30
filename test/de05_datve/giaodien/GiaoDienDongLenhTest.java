package de05_datve.giaodien;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de05_datve.model.TrangThaiGhe;
import de05_datve.nghiepvu.SoDoGhe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

// Kiểm F08: giao diện dòng lệnh xem sơ đồ ghế và chọn ghế, chỉ gọi tầng nghiệp vụ; bàn phím và màn hình được thay bằng chuỗi.
class GiaoDienDongLenhTest {

    private final Supplier<LocalDateTime> dongHo = () -> LocalDateTime.of(2026, 1, 1, 9, 0);

    private SoDoGhe soDoMoi(int tongSoGhe) {
        return new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
    }

    // Chạy giao diện với chuỗi nhập sẵn thay cho bàn phím, trả về mọi thứ đã in ra màn hình.
    private String chay(SoDoGhe soDo, String chuoiNhap) throws IOException {
        StringWriter manHinh = new StringWriter();
        try (BufferedReader vao = new BufferedReader(new StringReader(chuoiNhap));
                PrintWriter ra = new PrintWriter(manHinh)) {
            new GiaoDienDongLenh(soDo, vao, ra).chay();
        }
        return manHinh.toString();
    }

    @Test
    void inSoDoGheVoiBaTrangThai() throws IOException {
        SoDoGhe soDo = soDoMoi(12);
        soDo.giuGhe(1, "An");
        soDo.giuGhe(2, "Binh");
        soDo.thanhToan(2, "Binh");

        String manHinh = chay(soDo, "Cuong\n0\n");

        assertTrue(manHinh.contains("[ 0 .] [ 1 G] [ 2 X] [ 3 .]"), manHinh);
        assertTrue(manHinh.contains("[10 .] [11 .]"), manHinh);
    }

    @Test
    void hetDauVaoThiThoatKhongLoi() throws IOException {
        SoDoGhe soDo = soDoMoi(5);

        chay(soDo, "");
        String manHinh = chay(soDo, "An\n");

        assertTrue(manHinh.contains("[ 0 .]"), manHinh);
    }

    @Test
    void chonGheDeGiu() throws IOException {
        SoDoGhe soDo = soDoMoi(5);

        String manHinh = chay(soDo, "An\n1\n3\n0\n");

        assertEquals(TrangThaiGhe.DANG_GIU, soDo.chupSoDo()[3]);
        assertTrue(manHinh.contains("Thành công."), manHinh);
    }

    @Test
    void thanhToanVaHuyGiu() throws IOException {
        SoDoGhe soDo = soDoMoi(5);

        // giữ ghế 1, thanh toán ghế 1, giữ ghế 4, huỷ giữ ghế 4, thoát
        chay(soDo, "An\n1\n1\n2\n1\n1\n4\n3\n4\n0\n");

        assertEquals(TrangThaiGhe.DA_BAN, soDo.chupSoDo()[1]);
        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[4]);
    }

    @Test
    void thatBaiThiInLyDo() throws IOException {
        SoDoGhe soDo = soDoMoi(5);
        soDo.giuGhe(2, "Binh");

        // An giữ ghế 2, rồi An thanh toán ghế 2, cả hai đều thất bại vì Bình đang giữ
        String manHinh = chay(soDo, "An\n1\n2\n2\n2\n0\n");

        assertTrue(manHinh.contains("Không được: ghế đang có người giữ."), manHinh);
        assertTrue(manHinh.contains("Không được: ghế do người khác giữ."), manHinh);
    }

    @Test
    void nhapSoGheSaiThiBaoVaChayTiep() throws IOException {
        SoDoGhe soDo = soDoMoi(5);

        // giữ ghế "abc", giữ ghế để trống, giữ ghế 99, rồi vẫn thoát được bằng 0
        String manHinh = chay(soDo, "An\n1\nabc\n1\n\n1\n99\n0\n");

        assertEquals(2, soLanXuatHien(manHinh, "Số ghế phải là số nguyên."), manHinh);
        assertTrue(manHinh.contains("Không được: ghế không tồn tại."), manHinh);
        for (TrangThaiGhe t : soDo.chupSoDo()) {
            assertEquals(TrangThaiGhe.TRONG, t);
        }
    }

    @Test
    void nhapLuaChonSaiThiBaoLai() throws IOException {
        SoDoGhe soDo = soDoMoi(5);

        String manHinh = chay(soDo, "An\n7\nxyz\n0\n");

        assertEquals(2, soLanXuatHien(manHinh, "Lựa chọn không hợp lệ."), manHinh);
    }

    @Test
    void suatHetGheThiBao() throws IOException {
        SoDoGhe hetGhe = soDoMoi(3);
        for (int ghe = 0; ghe < 3; ghe++) {
            hetGhe.giuGhe(ghe, "Binh");
            hetGhe.thanhToan(ghe, "Binh");
        }
        SoDoGhe conMotGhe = soDoMoi(3);
        conMotGhe.giuGhe(0, "Binh");
        conMotGhe.giuGhe(1, "Binh");
        conMotGhe.thanhToan(1, "Binh");

        assertTrue(chay(hetGhe, "An\n0\n").contains("Suất đã hết ghế trống."));
        assertFalse(chay(conMotGhe, "An\n0\n").contains("Suất đã hết ghế trống."));
    }

    private int soLanXuatHien(String chuoi, String doan) {
        int dem = 0;
        int viTri = chuoi.indexOf(doan);
        while (viTri >= 0) {
            dem++;
            viTri = chuoi.indexOf(doan, viTri + doan.length());
        }
        return dem;
    }
}
