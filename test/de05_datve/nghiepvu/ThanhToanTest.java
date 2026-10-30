package de05_datve.nghiepvu;

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

// Kiểm F03: thanh toán ĐANG GIỮ → ĐÃ BÁN là một phép nguyên tử, dùng cùng khoá với giữ ghế.
class ThanhToanTest {

    private final Supplier<LocalDateTime> dongHo = () -> LocalDateTime.of(2026, 1, 1, 9, 0);

    @Test
    void thanhToanGheDangGiuThanhCong() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        assertEquals(new ThanhCong(), soDo.thanhToan(2, "An"));
        assertEquals(TrangThaiGhe.DA_BAN, soDo.chupSoDo()[2]);
    }

    @Test
    void thanhToanGheChuaAiGiuThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        assertEquals(new ThatBai(LyDo.CHUA_AI_GIU), soDo.thanhToan(2, "An"));
        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
    }

    @Test
    void thanhToanGheDaBanThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        soDo.thanhToan(2, "An");

        assertEquals(new ThatBai(LyDo.GHE_DA_BAN), soDo.thanhToan(2, "An"));
    }

    @Test
    void nguoiKhacThanhToanGheDangGiuThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        assertEquals(new ThatBai(LyDo.KHONG_PHAI_NGUOI_GIU), soDo.thanhToan(2, "Binh"));
        assertEquals(TrangThaiGhe.DANG_GIU, soDo.chupSoDo()[2]);
        assertEquals(new ThanhCong(), soDo.thanhToan(2, "An"));
    }

    @Test
    void chiNhanSoGheTuKhongDenTongTruMot() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(0, "An");
        soDo.giuGhe(4, "An");

        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.thanhToan(-1, "An"));
        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.thanhToan(5, "An"));
        assertEquals(new ThanhCong(), soDo.thanhToan(0, "An"));
        assertEquals(new ThanhCong(), soDo.thanhToan(4, "An"));
    }

    // An giữ cả 1000 ghế rồi bấm "Thanh toán" từ 200 luồng, mỗi luồng trả lần lượt ghế 0..999 theo cùng thứ tự.
    // Mỗi ghế chỉ được thu tiền một lần, nên tổng số lần thanh toán thành công phải đúng bằng số ghế.
    @Test
    void nhieuLuongCungThanhToanMotGheChiMotLanThanhCong() throws Exception {
        int soLuong = 200;
        int tongSoGhe = 1000;
        SoDoGhe soDo = new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        for (int ghe = 0; ghe < tongSoGhe; ghe++) {
            soDo.giuGhe(ghe, "An");
        }
        List<Future<Integer>> soLanThanhToanDuoc = new ArrayList<>();

        try (ExecutorService pool = Executors.newFixedThreadPool(soLuong)) {
            for (int i = 0; i < soLuong; i++) {
                Callable<Integer> bamThanhToan = () -> {
                    int dem = 0;
                    for (int ghe = 0; ghe < tongSoGhe; ghe++) {
                        if (soDo.thanhToan(ghe, "An") instanceof ThanhCong) {
                            dem++;
                        }
                    }
                    return dem;
                };
                soLanThanhToanDuoc.add(pool.submit(bamThanhToan));
            }

            int tong = 0;
            for (Future<Integer> f : soLanThanhToanDuoc) {
                tong += f.get();
            }
            assertEquals(tongSoGhe, tong);
        }
    }
}
