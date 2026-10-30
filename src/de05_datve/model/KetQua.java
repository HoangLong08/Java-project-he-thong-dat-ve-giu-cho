package de05_datve.model;

// Phục vụ F02, F03, F06: kiểu kết quả duy nhất mà mọi phép nghiệp vụ trả về (TC3 mức 4).
public sealed interface KetQua permits ThanhCong, ThatBai {
}
