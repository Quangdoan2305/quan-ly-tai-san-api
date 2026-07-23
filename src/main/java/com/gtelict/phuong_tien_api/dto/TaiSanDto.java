package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
public class TaiSanDto {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(example = "TS-001")
    private String maTaiSan;

    @Schema(example = "Màn hình Dell 27 inch")
    private String tenTaiSan;

    @Schema(example = "LTS01")
    private String maDanhMucLoai;

    @Schema(example = "Tên loại tài sản")
    private String tenLoaiTaiSan;

    private Map<String, Object> thongTinChiTiet;

    @Schema(example = "Đang sử dụng")
    private String tinhTrang;

    @Schema(example = "DV001")
    private String maDonViQuanLy;

    @Schema(example = "Phòng Cảnh sát giao thông")
    private String tenDonViQuanLy;

    @Schema(example = "CB001")
    private String maCanBoSuDung;

    @Schema(example = "Nguyễn Văn A")
    private String tenCanBoSuDung;

    @Schema(example = "CB002")
    private String maNguoiCapPhat;

    @Schema(example = "Trần Văn B")
    private String nguoiCapPhat;

    @Schema(example = "2024-05-15")
    private java.time.LocalDate ngayCapPhat;

    // Các trường đặc thù của Phương Tiện
    private String bienSo;
    private String soKhung;
    private String soMay;
    private String nhanHieu;

    // Các trường đặc thù của Thiết bị CNTT
    private String soSerial;
    private String diaChiMac;
    private String cauHinh;

    // Các trường đặc thù của Vũ Khí
    private String soHieu;
    private String namSanXuat;
}
