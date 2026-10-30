package de05_datve.model;

// Phục vụ F01: ba trạng thái của một ghế và phép chuyển hợp lệ giữa chúng (E03 thêm ĐÃ BÁN → TRỐNG sau).
public enum TrangThaiGhe {
    TRONG,
    DANG_GIU,
    DA_BAN;

    public boolean coTheChuyenSang(TrangThaiGhe moi) {
        throw new UnsupportedOperationException("Chưa làm: F01");
    }
}
