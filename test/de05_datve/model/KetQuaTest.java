package de05_datve.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

// Test mẫu: kiểm tra JUnit 5 chạy được trên kiểu kết quả dùng chung KetQua.
class KetQuaTest {

    @Test
    void thatBaiMangTheoLyDo() {
        KetQua kq = new ThatBai(LyDo.GHE_DA_BAN);

        ThatBai thatBai = assertInstanceOf(ThatBai.class, kq);
        assertEquals(LyDo.GHE_DA_BAN, thatBai.lyDo());
    }

    @Test
    void thanhCongLaMotKetQua() {
        KetQua kq = new ThanhCong();

        assertInstanceOf(ThanhCong.class, kq);
    }
}
