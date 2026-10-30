package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import de05_datve.model.LyDo;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// Kiểm F05: luồng nền định kỳ nhả ghế hết hạn, không chờ người bấm; dung() làm luồng dừng hẳn.
class BoNhaGheTuDongTest {

    private final DongHoGia dongHo = new DongHoGia(LocalDateTime.of(2026, 1, 1, 9, 0, 0));

    @Test
    void luongNenTuNhaGheHetHanKhongChoNguoiBam() throws Exception {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        dongHo.tien(Duration.ofMinutes(5)); // 9:05, sau đó không ai bấm gì

        BoNhaGheTuDong bo = new BoNhaGheTuDong(soDo, 10);
        bo.batDau();
        try {
            for (int lan = 0; lan < 200 && soDo.chupSoDo()[2] != TrangThaiGhe.TRONG; lan++) {
                Thread.sleep(10); // chờ tối đa 2 giây
            }
            assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
            assertEquals(new ThatBai(LyDo.CHUA_AI_GIU), soDo.thanhToan(2, "An"));
        } finally {
            bo.dung();
        }
    }

    @Test
    void sauKhiDungThiKhongNhaNua() throws Exception {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        BoNhaGheTuDong bo = new BoNhaGheTuDong(soDo, 10);
        bo.batDau();

        assertTimeoutPreemptively(Duration.ofSeconds(2), bo::dung); // dừng được, không treo

        dongHo.tien(Duration.ofMinutes(6)); // 9:06, phiếu đã hết hạn
        Thread.sleep(100); // bằng 10 chu kỳ, đủ để luồng nền nhả ghế nếu nó còn chạy
        assertEquals(TrangThaiGhe.DANG_GIU, soDo.chupSoDo()[2]);
    }
}
