package de05_datve.nghiepvu;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

// Đồng hồ giả cho test: đứng yên cho tới khi test gọi tien(...), nên test hết hạn 5 phút khỏi phải chờ.
class DongHoGia implements Supplier<LocalDateTime> {

    private LocalDateTime bayGio;

    DongHoGia(LocalDateTime batDau) {
        this.bayGio = batDau;
    }

    void tien(Duration khoang) {
        bayGio = bayGio.plus(khoang);
    }

    @Override
    public LocalDateTime get() {
        return bayGio;
    }
}
