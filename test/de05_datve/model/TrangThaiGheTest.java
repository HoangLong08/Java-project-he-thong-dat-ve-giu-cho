package de05_datve.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// Kiểm F01: phép chuyển trạng thái hợp lệ theo Hình 7.2 của đề.
class TrangThaiGheTest {

    @Test
    void trongChiChuyenSangDangGiu() {
        assertTrue(TrangThaiGhe.TRONG.coTheChuyenSang(TrangThaiGhe.DANG_GIU));
        assertFalse(TrangThaiGhe.TRONG.coTheChuyenSang(TrangThaiGhe.DA_BAN)); // chưa giữ thì không bán được
        assertFalse(TrangThaiGhe.TRONG.coTheChuyenSang(TrangThaiGhe.TRONG));
    }

    @Test
    void dangGiuChuyenSangDaBanHoacTrong() {
        assertTrue(TrangThaiGhe.DANG_GIU.coTheChuyenSang(TrangThaiGhe.DA_BAN));
        assertTrue(TrangThaiGhe.DANG_GIU.coTheChuyenSang(TrangThaiGhe.TRONG)); // hết hạn hoặc huỷ giữ
        assertFalse(TrangThaiGhe.DANG_GIU.coTheChuyenSang(TrangThaiGhe.DANG_GIU)); // không giữ ghế đang bị giữ
    }

    @Test
    void daBanLaTrangThaiCuoi() {
        assertFalse(TrangThaiGhe.DA_BAN.coTheChuyenSang(TrangThaiGhe.TRONG));
        assertFalse(TrangThaiGhe.DA_BAN.coTheChuyenSang(TrangThaiGhe.DANG_GIU));
        assertFalse(TrangThaiGhe.DA_BAN.coTheChuyenSang(TrangThaiGhe.DA_BAN)); // không bán một ghế hai lần
    }
}
