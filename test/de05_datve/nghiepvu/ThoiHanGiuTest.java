package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de05_datve.model.LyDo;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// Kiểm F04: giữ chỗ có thời hạn 5 phút, tính từ lúc giữ; dùng đồng hồ giả nên test không phải chờ.
class ThoiHanGiuTest {

    private final DongHoGia dongHo = new DongHoGia(LocalDateTime.of(2026, 1, 1, 9, 0, 0));

    @Test
    void thanhToanChiDuocTruocMocNamPhut() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(1, "An");
        soDo.giuGhe(2, "An");

        dongHo.tien(Duration.ofMinutes(4).plusSeconds(59)); // 9:04:59
        assertEquals(new ThanhCong(), soDo.thanhToan(1, "An"));

        dongHo.tien(Duration.ofSeconds(1)); // 9:05:00
        assertEquals(new ThatBai(LyDo.HET_HAN_GIU), soDo.thanhToan(2, "An"));
    }

    @Test
    void tinhHanTuLucGiuChuKhongPhaiLucTaoSoDo() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        dongHo.tien(Duration.ofMinutes(2)); // 9:02
        soDo.giuGhe(2, "An");
        dongHo.tien(Duration.ofMinutes(5)); // 9:07, đúng 5 phút sau lúc giữ

        assertEquals(new ThatBai(LyDo.HET_HAN_GIU), soDo.thanhToan(2, "An"));
    }

    @Test
    void dungThoiHanGiuCuaSoDo() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, Duration.ofSeconds(30));
        soDo.giuGhe(2, "An");

        dongHo.tien(Duration.ofSeconds(30)); // 9:00:30

        assertEquals(new ThatBai(LyDo.HET_HAN_GIU), soDo.thanhToan(2, "An"));
    }

    @Test
    void hetHanThiGheTroVeTrongNguoiKhacGiuDuoc() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        dongHo.tien(Duration.ofMinutes(6)); // 9:06
        assertEquals(new ThatBai(LyDo.HET_HAN_GIU), soDo.thanhToan(2, "An"));

        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
        assertEquals(new ThanhCong(), soDo.giuGhe(2, "Binh"));
    }
}
