package de05_datve.nghiepvu;

import java.util.concurrent.ConcurrentHashMap;

// Phục vụ E01 (mở rộng): nhiều suất chiếu, mỗi suất một SoDoGhe độc lập.
public class RapChieu {

    private final ConcurrentHashMap<String, SoDoGhe> cacSuat = new ConcurrentHashMap<>();

    public SoDoGhe suat(String maSuat) {
        throw new UnsupportedOperationException("Chưa làm: E01");
    }
}
