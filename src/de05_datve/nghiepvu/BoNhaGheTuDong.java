package de05_datve.nghiepvu;

// Phục vụ F05: luồng nền định kỳ gọi SoDoGhe.nhaGheHetHan() để nhả ghế giữ quá hạn mà không chờ người bấm.
public class BoNhaGheTuDong implements Runnable {

    private final SoDoGhe soDo;
    private final long chuKyMs;
    private volatile boolean dangChay;
    private Thread luong;

    public BoNhaGheTuDong(SoDoGhe soDo, long chuKyMs) {
        throw new UnsupportedOperationException("Chưa làm: F05");
    }

    public void batDau() {
        throw new UnsupportedOperationException("Chưa làm: F05");
    }

    public void dung() throws InterruptedException {
        throw new UnsupportedOperationException("Chưa làm: F05");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Chưa làm: F05");
    }
}
