package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChiTietBienBanDto {
    private TaiSanRutGonDto taiSan;
    
    @Schema(example = "Mới 100%")
    private String tinhTrangLucGiao;
    
    @Schema(example = "Giao từ Phòng cho đ/c Bình")
    private String ghiChu;
}
