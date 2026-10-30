package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de05_datve.model.LyDo;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

// Kiểm F06: người giữ tự huỷ, ghế ĐANG GIỮ → TRỐNG, dùng cùng khoá với giữ ghế và thanh toán.
class HuyGiuTest {

    private final DongHoGia dongHo = new DongHoGia(LocalDateTime.of(2026, 1, 1, 9, 0, 0));

    @Test
    void huyGheMinhDangGiuThanhCong() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        assertEquals(new ThanhCong(), soDo.huyGiu(2, "An"));
        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
        assertEquals(new ThanhCong(), soDo.giuGhe(2, "Binh"));
    }

    @Test
    void huyGheNguoiKhacGiuThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");

        assertEquals(new ThatBai(LyDo.KHONG_PHAI_NGUOI_GIU), soDo.huyGiu(2, "Binh"));
        assertEquals(TrangThaiGhe.DANG_GIU, soDo.chupSoDo()[2]);
        assertEquals(new ThanhCong(), soDo.thanhToan(2, "An"));
    }

    @Test
    void huyGheDaBanThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An");
        soDo.thanhToan(2, "An");

        assertEquals(new ThatBai(LyDo.GHE_DA_BAN), soDo.huyGiu(2, "An"));
        assertEquals(TrangThaiGhe.DA_BAN, soDo.chupSoDo()[2]);
    }

    @Test
    void huyGheChuaAiGiuThatBai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        assertEquals(new ThatBai(LyDo.CHUA_AI_GIU), soDo.huyGiu(2, "An"));
        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[2]);
    }

    @Test
    void chiNhanSoGheTuKhongDenTongTruMot() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(0, "An");
        soDo.giuGhe(4, "An");

        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.huyGiu(-1, "An"));
        assertEquals(new ThatBai(LyDo.GHE_KHONG_TON_TAI), soDo.huyGiu(5, "An"));
        assertEquals(new ThanhCong(), soDo.huyGiu(0, "An"));
        assertEquals(new ThanhCong(), soDo.huyGiu(4, "An"));
    }

    @Test
    void huyGiuXoaPhieuNenLuotQuetKhongDemLai() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        soDo.giuGhe(2, "An"); // 9:00
        dongHo.tien(Duration.ofMinutes(1));
        soDo.huyGiu(2, "An"); // 9:01

        dongHo.tien(Duration.ofMinutes(9)); // 9:10
        assertEquals(0, soDo.nhaGheHetHan());
    }

    // An giữ 1000 ghế rồi bấm cả "Thanh toán" lẫn "Huỷ": 100 luồng thanh toán và 100 luồng huỷ,
    // tất cả đi qua ghế 0..999 theo cùng thứ tự. Mỗi ghế chỉ được đúng một phép thành công,
    // và trạng thái cuối phải khớp: ghế thanh toán được thì ĐÃ BÁN, ghế huỷ được thì TRỐNG.
    @Test
    void huyVaThanhToanCungLucMoiGheChiMotPhepThanhCong() throws Exception {
        int soLuongMoiLoai = 100;
        int tongSoGhe = 1000;
        SoDoGhe soDo = new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        for (int ghe = 0; ghe < tongSoGhe; ghe++) {
            soDo.giuGhe(ghe, "An");
        }
        List<Future<Integer>> thanhToanDuoc = new ArrayList<>();
        List<Future<Integer>> huyDuoc = new ArrayList<>();

        try (ExecutorService pool = Executors.newFixedThreadPool(2 * soLuongMoiLoai)) {
            for (int i = 0; i < soLuongMoiLoai; i++) {
                Callable<Integer> bamThanhToan = () -> {
                    int dem = 0;
                    for (int ghe = 0; ghe < tongSoGhe; ghe++) {
                        if (soDo.thanhToan(ghe, "An") instanceof ThanhCong) {
                            dem++;
                        }
                    }
                    return dem;
                };
                Callable<Integer> bamHuy = () -> {
                    int dem = 0;
                    for (int ghe = 0; ghe < tongSoGhe; ghe++) {
                        if (soDo.huyGiu(ghe, "An") instanceof ThanhCong) {
                            dem++;
                        }
                    }
                    return dem;
                };
                thanhToanDuoc.add(pool.submit(bamThanhToan));
                huyDuoc.add(pool.submit(bamHuy));
            }

            int tongThanhToan = 0;
            for (Future<Integer> f : thanhToanDuoc) {
                tongThanhToan += f.get();
            }
            int tongHuy = 0;
            for (Future<Integer> f : huyDuoc) {
                tongHuy += f.get();
            }
            assertEquals(tongSoGhe, tongThanhToan + tongHuy);

            int soGheDaBan = 0;
            int soGheTrong = 0;
            for (TrangThaiGhe t : soDo.chupSoDo()) {
                if (t == TrangThaiGhe.DA_BAN) {
                    soGheDaBan++;
                } else if (t == TrangThaiGhe.TRONG) {
                    soGheTrong++;
                }
            }
            assertEquals(tongThanhToan, soGheDaBan);
            assertEquals(tongHuy, soGheTrong);
        }
    }
}
