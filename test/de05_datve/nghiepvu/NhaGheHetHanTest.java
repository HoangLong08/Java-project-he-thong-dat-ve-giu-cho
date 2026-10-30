package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de05_datve.model.KetQua;
import de05_datve.model.LyDo;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

// Kiểm F05: nhaGheHetHan() nhả mọi ghế giữ quá hạn về TRỐNG mà không chờ người bấm.
class NhaGheHetHanTest {

    private final DongHoGia dongHo = new DongHoGia(LocalDateTime.of(2026, 1, 1, 9, 0, 0));

    @Test
    void nhaCacGheDaHetHanVaTraSoGheNha() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(0, "An"); // 9:00
        dongHo.tien(Duration.ofMinutes(1));
        soDo.giuGhe(1, "An"); // 9:01
        dongHo.tien(Duration.ofMinutes(1));
        soDo.giuGhe(2, "An"); // 9:02

        dongHo.tien(Duration.ofMinutes(4)); // 9:06: ghế 0 giữ 6 phút, ghế 1 đúng 5 phút, ghế 2 mới 4 phút
        assertEquals(2, soDo.nhaGheHetHan());

        TrangThaiGhe[] mongDoi = {
            TrangThaiGhe.TRONG, TrangThaiGhe.TRONG, TrangThaiGhe.DANG_GIU, TrangThaiGhe.TRONG, TrangThaiGhe.TRONG
        };
        assertArrayEquals(mongDoi, soDo.chupSoDo());
    }

    @Test
    void thanhToanSauKhiDaNhaThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        dongHo.tien(Duration.ofMinutes(5)); // 9:05
        soDo.nhaGheHetHan();

        assertEquals(new ThatBai(LyDo.CHUA_AI_GIU), soDo.thanhToan(2, "An"));
        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
    }

    @Test
    void gheDaBanKhongBiNhaKhiQuaHan() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        dongHo.tien(Duration.ofMinutes(1));
        soDo.thanhToan(2, "An"); // 9:01

        dongHo.tien(Duration.ofMinutes(9)); // 9:10
        assertEquals(0, soDo.nhaGheHetHan());
        assertEquals(TrangThaiGhe.DA_BAN, soDo.chupSoDo()[2]);
    }

    @Test
    void gheThanhToanQuaHanDaNhaKhongBiDemLai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        dongHo.tien(Duration.ofMinutes(6)); // 9:06
        assertEquals(new ThatBai(LyDo.HET_HAN_GIU), soDo.thanhToan(2, "An")); // thanhToan đã tự nhả ghế 2

        assertEquals(0, soDo.nhaGheHetHan());
    }

    // An giữ cả 2000 ghế lúc 9:00. Một luồng trả tiền lần lượt từng ghế, một luồng liên tục quét nhả ghế hết hạn.
    // Ngay khi An trả xong ghế 0, test vặn đồng hồ sang 9:05: mọi phiếu chưa trả tiền hết hạn đúng lúc An đang trả.
    // Dù hai luồng xen nhau thế nào, mỗi ghế phải khớp: trả tiền thành công thì ĐÃ BÁN, thất bại thì TRỐNG.
    @Test
    void nhaDungLucNguoiGiuDangThanhToan() throws Exception {
        int tongSoGhe = 2000;
        SoDoGhe soDo = new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        for (int ghe = 0; ghe < tongSoGhe; ghe++) {
            soDo.giuGhe(ghe, "An");
        }
        KetQua[] ketQua = new KetQua[tongSoGhe];

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            Future<?> traTien = pool.submit(() -> {
                for (int ghe = 0; ghe < tongSoGhe; ghe++) {
                    ketQua[ghe] = soDo.thanhToan(ghe, "An");
                }
            });
            Future<?> quetNha = pool.submit(() -> {
                while (soDo.chupSoDo()[tongSoGhe - 1] == TrangThaiGhe.DANG_GIU) {
                    soDo.nhaGheHetHan();
                }
            });

            while (soDo.chupSoDo()[0] != TrangThaiGhe.DA_BAN) {
                // chờ An trả xong ghế 0
            }
            dongHo.tien(Duration.ofMinutes(5)); // 9:05
            traTien.get();
            quetNha.get();
        }

        TrangThaiGhe[] cuoi = soDo.chupSoDo();
        int daBan = 0;
        for (int ghe = 0; ghe < tongSoGhe; ghe++) {
            if (ketQua[ghe] instanceof ThanhCong) {
                assertEquals(TrangThaiGhe.DA_BAN, cuoi[ghe], "ghế " + ghe);
                daBan++;
            } else {
                assertEquals(TrangThaiGhe.TRONG, cuoi[ghe], "ghế " + ghe);
            }
        }
        assertTrue(daBan > 0 && daBan < tongSoGhe, "phải có cả ghế bán được lẫn ghế bị nhả, đã bán " + daBan);
    }
}
