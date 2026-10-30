package de05_datve.nghiepvu;

import de05_datve.model.BaoCao;
import de05_datve.model.KetQua;
import de05_datve.model.LyDo;
import de05_datve.model.PhieuGiu;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
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
        synchronized (khoa) {
            if (soGhe < 0 || soGhe >= trangThai.length) {
                return new ThatBai(LyDo.GHE_KHONG_TON_TAI);
            }
            if (trangThai[soGhe] == TrangThaiGhe.DA_BAN) {
                return new ThatBai(LyDo.GHE_DA_BAN);
            }
            if (!trangThai[soGhe].coTheChuyenSang(TrangThaiGhe.DANG_GIU)) {
                return new ThatBai(LyDo.GHE_DANG_BI_GIU);
            }
            trangThai[soGhe] = TrangThaiGhe.DANG_GIU;
            phieuGiu.put(soGhe, new PhieuGiu(soGhe, nguoi, dongHo.get()));
            return new ThanhCong();
        }
    }

    public KetQua thanhToan(int soGhe, String nguoi) {
        synchronized (khoa) {
            if (soGhe < 0 || soGhe >= trangThai.length) {
                return new ThatBai(LyDo.GHE_KHONG_TON_TAI);
            }
            if (trangThai[soGhe] == TrangThaiGhe.TRONG) {
                return new ThatBai(LyDo.CHUA_AI_GIU);
            }
            if (trangThai[soGhe] == TrangThaiGhe.DA_BAN) {
                return new ThatBai(LyDo.GHE_DA_BAN);
            }
            if (!phieuGiu.get(soGhe).nguoiGiu().equals(nguoi)) {
                return new ThatBai(LyDo.KHONG_PHAI_NGUOI_GIU);
            }
            if (phieuGiu.get(soGhe).hetHan(dongHo.get(), thoiHanGiu)) {
                trangThai[soGhe] = TrangThaiGhe.TRONG;
                return new ThatBai(LyDo.HET_HAN_GIU);
            }
            trangThai[soGhe] = TrangThaiGhe.DA_BAN;
            return new ThanhCong();
        }
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
