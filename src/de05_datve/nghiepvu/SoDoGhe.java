package de05_datve.nghiepvu;

import de05_datve.model.BaoCao;
import de05_datve.model.KetQua;
import de05_datve.model.PhieuGiu;
import de05_datve.model.TrangThaiGhe;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// Phục vụ F01–F04, F06, F09: sơ đồ ghế một suất chiếu; giữ, thanh toán, huỷ giữ, nhả ghế hết hạn, báo cáo, tất cả dưới một khoá.
public class SoDoGhe {

    public static final Duration THOI_HAN_GIU_MAC_DINH = Duration.ofMinutes(5);

    private final TrangThaiGhe[] trangThai;
    private final Map<Integer, PhieuGiu> phieuGiu = new HashMap<>();
    private final Supplier<LocalDateTime> dongHo;
    private final Duration thoiHanGiu;
    private final Object khoa = new Object();

    public SoDoGhe(int tongSoGhe, Supplier<LocalDateTime> dongHo, Duration thoiHanGiu) {
        if (tongSoGhe <= 0) {
            throw new IllegalArgumentException("Tổng số ghế phải lớn hơn 0: " + tongSoGhe);
        }
        this.trangThai = new TrangThaiGhe[tongSoGhe];
        for (int i = 0; i < tongSoGhe; i++) {
            trangThai[i] = TrangThaiGhe.TRONG;
        }
        this.dongHo = dongHo;
        this.thoiHanGiu = thoiHanGiu;
    }

    public KetQua giuGhe(int soGhe, String nguoi) {
        throw new UnsupportedOperationException("Chưa làm: F02");
    }

    public KetQua thanhToan(int soGhe, String nguoi) {
        throw new UnsupportedOperationException("Chưa làm: F03");
    }

    public KetQua huyGiu(int soGhe, String nguoi) {
        throw new UnsupportedOperationException("Chưa làm: F06");
    }

    public int nhaGheHetHan() {
        throw new UnsupportedOperationException("Chưa làm: F05");
    }

    public TrangThaiGhe[] chupSoDo() {
        synchronized (khoa) {
            return Arrays.copyOf(trangThai, trangThai.length);
        }
    }

    public BaoCao baoCao() {
        throw new UnsupportedOperationException("Chưa làm: F09");
    }
}
