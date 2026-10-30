package de05_datve.nghiepvu;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de05_datve.model.TrangThaiGhe;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

// Kiểm F01: sơ đồ ghế của một suất chiếu, ghế đánh số từ 0 đến tổng - 1.
class SoDoGheTest {

    private final Supplier<LocalDateTime> dongHo = () -> LocalDateTime.of(2026, 1, 1, 9, 0);

    @Test
    void soDoMoiCoDungSoGheVaDeuTrong() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        TrangThaiGhe[] mongDoi = {
            TrangThaiGhe.TRONG, TrangThaiGhe.TRONG, TrangThaiGhe.TRONG, TrangThaiGhe.TRONG, TrangThaiGhe.TRONG
        };
        assertArrayEquals(mongDoi, soDo.chupSoDo());
    }

    @Test
    void suaBanChupKhongDoiSoDo() {
        SoDoGhe soDo = new SoDoGhe(5, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH);

        TrangThaiGhe[] banChup = soDo.chupSoDo();
        banChup[0] = TrangThaiGhe.DA_BAN;

        assertEquals(TrangThaiGhe.TRONG, soDo.chupSoDo()[0]);
    }

    @Test
    void tongSoGheBangKhongBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new SoDoGhe(0, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH));
    }

    @Test
    void tongSoGheAmBiTuChoi() {
        assertThrows(IllegalArgumentException.class,
                () -> new SoDoGhe(-1, dongHo, SoDoGhe.THOI_HAN_GIU_MAC_DINH));
    }
}
