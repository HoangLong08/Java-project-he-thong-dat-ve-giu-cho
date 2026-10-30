package de05_datve.model;

// Phục vụ F02, F03, F05, F06: kết quả khi phép nghiệp vụ thất bại, kèm lý do.
public record ThatBai(LyDo lyDo) implements KetQua {
}
