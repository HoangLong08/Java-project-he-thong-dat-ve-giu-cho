package de05_datve.model;

// Phục vụ F01: ba trạng thái của một ghế và phép chuyển hợp lệ giữa chúng (E03 thêm ĐÃ BÁN → TRỐNG sau).
public enum TrangThaiGhe {
    TRONG,
    DANG_GIU,
    DA_BAN;

    public boolean coTheChuyenSang(TrangThaiGhe moi) {
        return switch (this) {
            case TRONG -> moi == DANG_GIU;
            case DANG_GIU -> moi == DA_BAN || moi == TRONG;
            case DA_BAN -> false;
        };
    }
}
