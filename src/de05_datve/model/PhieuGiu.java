package de05_datve.model;

import java.time.Duration;
import java.time.LocalDateTime;

// Phục vụ F04, F05: ghi ai đang giữ ghế nào và giữ từ lúc nào, để biết khi nào quá hạn 5 phút.
public record PhieuGiu(int soGhe, String nguoiGiu, LocalDateTime lucGiu) {

    public boolean hetHan(LocalDateTime bayGio, Duration thoiHan) {
        throw new UnsupportedOperationException("Chưa làm: F04");
    }
}
