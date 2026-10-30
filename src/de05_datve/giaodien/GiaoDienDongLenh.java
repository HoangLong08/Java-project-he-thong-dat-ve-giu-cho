package de05_datve.giaodien;

import de05_datve.model.KetQua;
import de05_datve.model.ThanhCong;
import de05_datve.model.ThatBai;
import de05_datve.model.TrangThaiGhe;
import de05_datve.nghiepvu.BoNhaGheTuDong;
import de05_datve.nghiepvu.SoDoGhe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;

// Phục vụ F08, F09: giao diện dòng lệnh xem sơ đồ ghế, chọn ghế, giữ, thanh toán, huỷ giữ, in báo cáo; chỉ gọi tầng nghiệp vụ.
public class GiaoDienDongLenh {

    private final SoDoGhe soDo;
    private final BufferedReader vao;
    private final PrintWriter ra;

    public GiaoDienDongLenh(SoDoGhe soDo, BufferedReader vao, PrintWriter ra) {
        this.soDo = soDo;
        this.vao = vao;
        this.ra = ra;
    }

    // Đọc tên một lần, rồi lặp: in sơ đồ, in menu, làm theo lựa chọn; dừng khi chọn 0 hoặc hết dữ liệu nhập.
    public void chay() throws IOException {
        ra.print("Tên của bạn: ");
        ra.flush();
        String nguoi = vao.readLine();
        if (nguoi == null) {
            return;
        }
        while (true) {
            inSoDo();
            ra.println("1) Giữ ghế   2) Thanh toán   3) Huỷ giữ   0) Thoát");
            ra.print("Chọn: ");
            ra.flush();
            String chon = vao.readLine();
            if (chon == null) {
                return;
            }
            String luaChon = chon.trim();
            if (luaChon.equals("0")) {
                return;
            }
            switch (luaChon) {
                case "1", "2", "3" -> {
                    ra.print("Số ghế: ");
                    ra.flush();
                    String dong = vao.readLine();
                    if (dong == null) {
                        return;
                    }
                    try {
                        int soGhe = Integer.parseInt(dong.trim());
                        KetQua kq = switch (luaChon) {
                            case "1" -> soDo.giuGhe(soGhe, nguoi);
                            case "2" -> soDo.thanhToan(soGhe, nguoi);
                            default -> soDo.huyGiu(soGhe, nguoi);
                        };
                        inKetQua(kq);
                    } catch (NumberFormatException e) {
                        ra.println("Số ghế phải là số nguyên.");
                    }
                }
                default -> ra.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    // Mỗi ghế in thành [số ký hiệu], mười ghế một hàng.
    private void inSoDo() {
        TrangThaiGhe[] ghe = soDo.chupSoDo();
        ra.println("Sơ đồ ghế (. trống, G đang giữ, X đã bán):");
        int soGheTrong = 0;
        for (int i = 0; i < ghe.length; i++) {
            if (ghe[i] == TrangThaiGhe.TRONG) {
                soGheTrong++;
            }
            String kyHieu = switch (ghe[i]) {
                case TRONG -> ".";
                case DANG_GIU -> "G";
                case DA_BAN -> "X";
            };
            ra.print("[" + (i < 10 ? " " : "") + i + " " + kyHieu + "]");
            if ((i + 1) % 10 == 0 || i == ghe.length - 1) {
                ra.println();
            } else {
                ra.print(" ");
            }
        }
        if (soGheTrong == 0) {
            ra.println("Suất đã hết ghế trống.");
        }
    }

    private void inBaoCao() {
        throw new UnsupportedOperationException("Chưa làm: F09");
    }

    private void inKetQua(KetQua kq) {
        switch (kq) {
            case ThanhCong tc -> ra.println("Thành công.");
            case ThatBai tb -> {
                String lyDo = switch (tb.lyDo()) {
                    case GHE_KHONG_TON_TAI -> "ghế không tồn tại";
                    case GHE_DANG_BI_GIU -> "ghế đang có người giữ";
                    case GHE_DA_BAN -> "ghế đã bán";
                    case CHUA_AI_GIU -> "ghế chưa được giữ hoặc đã hết hạn giữ";
                    case KHONG_PHAI_NGUOI_GIU -> "ghế do người khác giữ";
                    case HET_HAN_GIU -> "đã quá 5 phút giữ chỗ, ghế đã được nhả";
                };
                ra.println("Không được: " + lyDo + ".");
            }
        }
    }

    // Một suất 20 ghế; luồng nền nhả ghế giữ quá 5 phút trong lúc người dùng thao tác; thoát thì dừng luồng nền.
    public static void main(String[] args) throws IOException, InterruptedException {
        SoDoGhe soDo = new SoDoGhe(20, LocalDateTime::now, SoDoGhe.THOI_HAN_GIU_MAC_DINH);
        BoNhaGheTuDong boNha = new BoNhaGheTuDong(soDo, 1000);
        boNha.batDau();
        try (BufferedReader vao = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter ra = new PrintWriter(System.out, true)) {
            new GiaoDienDongLenh(soDo, vao, ra).chay();
        } finally {
            boNha.dung();
        }
    }
}
