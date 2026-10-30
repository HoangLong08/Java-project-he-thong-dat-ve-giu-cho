package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de05_datve.model.KetQua;
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

import org.junit.jupiter.api.RepeatedTest;

// Kiểm F07: phép thử nhiều luồng của nhóm, giữ, huỷ, hết hạn, thanh toán đan xen; mỗi phép thử chạy lặp 5 lần.
class NhieuLuongDanXenTest {

    private final DongHoGia dongHo = new DongHoGia(LocalDateTime.of(2026, 1, 1, 9, 0, 0));
    private volatile boolean batDau; // cờ xuất phát: các luồng chờ cờ này để cùng ùa vào một lúc

    // Phép thử của đề: 200 người cùng bấm giữ ghế 3, mỗi người một Thread; chỉ đúng một người giữ được.
    @RepeatedTest(5)
    void haiTramNguoiBamCungMotGheChiMotNguoiGiuDuoc() throws InterruptedException {
        int soNguoi = 200;
        SoDoGhe soDo = new SoDoGhe(10, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        KetQua[] ketQua = new KetQua[soNguoi];
        Thread[] luong = new Thread[soNguoi];
        for (int i = 0; i < soNguoi; i++) {
            int nguoi = i;
            luong[i] = new Thread(() -> {
                while (!batDau) {
                    // chờ cờ xuất phát
                }
                ketQua[nguoi] = soDo.giuGhe(3, "Nguoi" + nguoi);
            });
        }
        for (Thread t : luong) {
            t.start();
        }
        batDau = true;
        for (Thread t : luong) {
            t.join();
        }

        int nguoiThang = -1;
        int soNguoiThang = 0;
        for (int i = 0; i < soNguoi; i++) {
            if (ketQua[i] instanceof ThanhCong) {
                nguoiThang = i;
                soNguoiThang++;
            } else {
                assertEquals(new ThatBai(LyDo.GHE_DANG_BI_GIU), ketQua[i]);
            }
        }
        assertEquals(1, soNguoiThang);

        int nguoiThua = (nguoiThang + 1) % soNguoi;
        assertEquals(new ThatBai(LyDo.KHONG_PHAI_NGUOI_GIU), soDo.thanhToan(3, "Nguoi" + nguoiThua));
        assertEquals(new ThanhCong(), soDo.thanhToan(3, "Nguoi" + nguoiThang));
    }

    // 100 người giữ 100 ghế lúc 9:00; đến 9:05 cả 100 cùng trả tiền trong khi luồng nền đang nhả ghế hết hạn.
    // Ai trả trước luồng nền thì nhận HET_HAN_GIU, ai trả sau thì ghế đã TRỐNG nên nhận CHUA_AI_GIU; không ai mua được.
    @RepeatedTest(5)
    void thanhToanSauKhiHetHanKhiCoNhieuLuong() throws Exception {
        int soNguoi = 100;
        SoDoGhe soDo = new SoDoGhe(soNguoi, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        for (int i = 0; i < soNguoi; i++) {
            soDo.giuGhe(i, "Nguoi" + i);
        }
        dongHo.tien(Duration.ofMinutes(5)); // 9:05

        BoNhaGheTuDong bo = new BoNhaGheTuDong(soDo, 1);
        bo.batDau();
        try (ExecutorService pool = Executors.newFixedThreadPool(soNguoi)) {
            List<Future<KetQua>> ketQua = new ArrayList<>();
            for (int i = 0; i < soNguoi; i++) {
                int ghe = i;
                Callable<KetQua> traTien = () -> soDo.thanhToan(ghe, "Nguoi" + ghe);
                ketQua.add(pool.submit(traTien));
            }
            for (Future<KetQua> f : ketQua) {
                KetQua kq = f.get();
                assertTrue(kq.equals(new ThatBai(LyDo.HET_HAN_GIU)) || kq.equals(new ThatBai(LyDo.CHUA_AI_GIU)),
                        "thanh toán sau khi hết hạn mà nhận " + kq);
            }
        } finally {
            bo.dung();
        }

        for (TrangThaiGhe t : soDo.chupSoDo()) {
            assertEquals(TrangThaiGhe.TRONG, t);
        }
    }

    // 20 người cùng giữ, huỷ, thanh toán trên 500 ghế; một luồng vặn đồng hồ nên phiếu giữ hết hạn giữa chừng,
    // luồng nền nhả ghế hết hạn. Dù xen nhau thế nào: không ghế nào bán hai lần, ghế ĐÃ BÁN khớp với lần
    // thanh toán thành công, và khi mọi phiếu đã hết hạn thì không còn ghế nào kẹt ở ĐANG GIỮ.
    @RepeatedTest(5)
    void giuHuyHetHanThanhToanDanXen() throws Exception {
        int tongSoGhe = 500;
        int soNguoi = 20;
        int soLuot = 500;
        SoDoGhe soDo = new SoDoGhe(tongSoGhe, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        int[][] thanhToanDuoc = new int[soNguoi][tongSoGhe]; // mỗi người chỉ ghi vào hàng của mình

        BoNhaGheTuDong bo = new BoNhaGheTuDong(soDo, 1);
        bo.batDau();
        try (ExecutorService pool = Executors.newFixedThreadPool(soNguoi + 1)) {
            List<Future<?>> viec = new ArrayList<>();
            Callable<Integer> vanDongHo = () -> {
                for (int i = 0; i < 50; i++) {
                    dongHo.tien(Duration.ofMinutes(1));
                    Thread.sleep(1);
                }
                return 0;
            };
            viec.add(pool.submit(vanDongHo));

            for (int p = 0; p < soNguoi; p++) {
                int nguoiSo = p;
                String nguoi = "Nguoi" + p;
                viec.add(pool.submit(() -> {
                    List<Integer> deThanhToanSau = new ArrayList<>();
                    for (int luot = 0; luot < soLuot; luot++) {
                        int ghe = (nguoiSo * 31 + luot * 7) % tongSoGhe;
                        if (soDo.giuGhe(ghe, nguoi) instanceof ThanhCong) {
                            switch (luot % 4) {
                                case 0 -> {
                                    if (soDo.thanhToan(ghe, nguoi) instanceof ThanhCong) {
                                        thanhToanDuoc[nguoiSo][ghe]++;
                                    }
                                }
                                case 1 -> soDo.huyGiu(ghe, nguoi);
                                case 2 -> deThanhToanSau.add(ghe);
                                default -> {
                                    // bỏ mặc cho phiếu hết hạn
                                }
                            }
                        }
                    }
                    for (int ghe : deThanhToanSau) {
                        if (soDo.thanhToan(ghe, nguoi) instanceof ThanhCong) {
                            thanhToanDuoc[nguoiSo][ghe]++;
                        }
                    }
                }));
            }

            for (Future<?> f : viec) {
                f.get();
            }
        } finally {
            bo.dung();
        }

        dongHo.tien(Duration.ofMinutes(10)); // mọi phiếu còn lại đều đã hết hạn
        soDo.nhaGheHetHan();
        TrangThaiGhe[] cuoi = soDo.chupSoDo();
        for (int ghe = 0; ghe < tongSoGhe; ghe++) {
            int soLanBan = 0;
            for (int p = 0; p < soNguoi; p++) {
                soLanBan += thanhToanDuoc[p][ghe];
            }
            assertTrue(soLanBan <= 1, "ghế " + ghe + " bị bán " + soLanBan + " lần");
            TrangThaiGhe mongDoi = soLanBan == 1 ? TrangThaiGhe.DA_BAN : TrangThaiGhe.TRONG;
            assertEquals(mongDoi, cuoi[ghe], "ghế " + ghe);
        }
    }
}
