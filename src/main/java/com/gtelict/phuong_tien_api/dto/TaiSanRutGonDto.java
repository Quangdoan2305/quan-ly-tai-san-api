package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
public class TaiSanRutGonDto {
    @Schema(example = "TS-001")
    private String maTaiSan;
    
    @Schema(example = "Màn hình Dell 27 inch")
    private String tenTaiSan;
    
    private Map<String, Object> thongTinChiTiet;

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
