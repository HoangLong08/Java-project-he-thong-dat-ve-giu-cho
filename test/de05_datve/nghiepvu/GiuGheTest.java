package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de05_datve.model.LyDo;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

// Kiểm F02: giữ ghế TRỐNG → ĐANG GIỮ là một phép nguyên tử.
class GiuGheTest {

    private final Supplier<LocalDateTime> dongHo = () -> LocalDateTime.of(2026, 1, 1, 9, 0);

    @Test
    void giuGheTrongThanhCong() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        assertEquals(new ThanhCong(), soDo.giuGhe(2, "An"));

        TrangThaiGhe[] mongDoi = {
            TrangThaiGhe.TRONG, TrangThaiGhe.TRONG, TrangThaiGhe.DANG_GIU, TrangThaiGhe.TRONG, TrangThaiGhe.TRONG
        };
        assertArrayEquals(mongDoi, soDo.chupSoDo());
    }

    @Test
    void giuGheDangBiGiuThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        assertEquals(new ThatBai(LyDo.GHE_DANG_BI_GIU), soDo.giuGhe(2, "Binh"));
        assertEquals(TrangThaiGhe.DANG_GIU, soDo.chupSoDo()[2]);
    }

    @Test
    void giuGheDaBanThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        soDo.thanhToan(2, "An");

        assertEquals(new ThatBai(LyDo.GHE_DA_BAN), soDo.giuGhe(2, "Binh"));
        assertEquals(TrangThaiGhe.DA_BAN, soDo.chupSoDo()[2]);
    }

    @Test
    void chiNhanSoGheTuKhongDenTongTruMot() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.giuGhe(-1, "An"));
        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.giuGhe(5, "An"));
        assertEquals(new ThanhCong(), soDo.giuGhe(0, "An"));
        assertEquals(new ThanhCong(), soDo.giuGhe(4, "An"));
    }

    // 200 người cùng lần lượt bấm giữ ghế 0..999 theo cùng thứ tự, nên ghế nào cũng có nhiều người bấm cùng lúc.
    // Mỗi ghế chỉ một người giữ được, nên tổng số lần giữ thành công phải đúng bằng số ghế.
    @Test
    void nhieuNguoiCungBamMotGheChiMotNguoiGiuDuoc() throws Exception {
        int soNguoi = 200;
        int tongSoGhe = 1000;
        SoDoGhe soDo = new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        List<Future<Integer>> soLanGiuDuoc = new ArrayList<>();

        try (ExecutorService pool = Executors.newFixedThreadPool(soNguoi)) {
            for (int i = 0; i < soNguoi; i++) {
                String nguoi = "Nguoi" + i;
                Callable<Integer> bamGiu = () -> {
                    int dem = 0;
                    for (int ghe = 0; ghe < tongSoGhe; ghe++) {
                        if (soDo.giuGhe(ghe, nguoi) instanceof ThanhCong) {
                            dem++;
                        }
                    }
                    return dem;
                };
                soLanGiuDuoc.add(pool.submit(bamGiu));
            }

            int tong = 0;
            for (Future<Integer> f : soLanGiuDuoc) {
                tong += f.get();
            }
            assertEquals(tongSoGhe, tong);
        }
    }
}
