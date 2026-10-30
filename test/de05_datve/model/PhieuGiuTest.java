package de05_datve.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// Kiểm F04: phiếu giữ còn hạn trong [lúc giữ, lúc giữ + thời hạn), đúng mốc thời hạn là hết hạn.
class PhieuGiuTest {

    private final PhieuGiu phieu = new PhieuGiu(2, "An", LocalDateTime.of(2026, 1, 1, 9, 0, 0));
    private final Duration namPhut = Duration.ofMinutes(5);

    @Test
    void conHanTruocMocNamPhut() {
        assertFalse(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 0, 0), namPhut));
        assertFalse(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 4, 59), namPhut));
    }

    @Test
    void hetHanDungMocNamPhut() {
        assertTrue(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 5, 0), namPhut));
    }

    @Test
    void hetHanSauMocNamPhut() {
        assertTrue(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 5, 1), namPhut));
    }

    @Test
    void dungThoiHanTruyenVao() {
        Duration baMuoiGiay = Duration.ofSeconds(30);

        assertFalse(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 0, 29), baMuoiGiay));
        assertTrue(phieu.hetHan(LocalDateTime.of(2026, 1, 1, 9, 0, 30), baMuoiGiay));
    }
}
