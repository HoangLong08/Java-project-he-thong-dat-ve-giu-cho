package de05_datve.model;

// Phục vụ F09: số vé đã bán, số ghế đang giữ và số ghế còn trống của một suất chiếu.
public record BaoCao(long daBan, long dangGiu, long conTrong) {

    public long tong() {
        throw new UnsupportedOperationException("Chưa làm: F09");
    }
}
