package de05_datve.nghiepvu;

// Phục vụ F05: luồng nền định kỳ gọi SoDoGhe.nhaGheHetHan() để nhả ghế giữ quá hạn mà không chờ người bấm.
public class BoNhaGheTuDong implements Runnable {

    private final SoDoGhe soDo;
    private final long chuKyMs;
    private volatile boolean dangChay;
    private Thread luong;

    public BoNhaGheTuDong(SoDoGhe soDo, long chuKyMs) {
        this.soDo = soDo;
        this.chuKyMs = chuKyMs;
    }

    public void batDau() {
        dangChay = true;
        luong = new Thread(this);
        luong.start();
    }

    // Tắt cờ rồi chờ luồng nền kết thúc; luồng chỉ thấy cờ sau giấc sleep hiện tại nên chờ tối đa một chu kỳ.
    public void dung() throws InterruptedException {
        dangChay = false;
        luong.join();
    }

    @Override
    public void run() {
        while (dangChay) {
            try {
                Thread.sleep(chuKyMs);
            } catch (InterruptedException e) {
                return;
            }
            soDo.nhaGheHetHan();
        }
    }
}
